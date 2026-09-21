package com.trainingdepot.gear.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import com.trainingdepot.gear.exception.ProductNotFoundException;
import com.trainingdepot.gear.kit.Kit;
import com.trainingdepot.gear.kit.KitView;
import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.repository.ProductRepository;
import com.trainingdepot.gear.service.impl.KitServiceImpl;

@ExtendWith(MockitoExtension.class)
class KitServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private KitServiceImpl kitService;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        kitService = new KitServiceImpl(productRepository);
        session = new MockHttpSession();
    }

    private Product product(int id, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName("Test Product " + id);
        p.setPrice(new BigDecimal("100"));
        p.setCategory(EquipmentCategory.GYM_ACCESSORIES);
        p.setStockQuantity(stock);
        p.setActive(true);
        return p;
    }

    @Test
    void add_thenView_resolvesAgainstLiveProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product(1, 10)));

        kitService.add(session, 1, 3);
        KitView view = kitService.view(session);

        assertThat(view.getLines()).hasSize(1);
        assertThat(view.getLines().get(0).getQuantity()).isEqualTo(3);
        assertThat(view.getSubtotal()).isEqualByComparingTo("300.00");
    }

    @Test
    void duplicateAdd_incrementsExistingLineRatherThanCreatingANewOne() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product(1, 10)));

        kitService.add(session, 1, 1);
        kitService.add(session, 1, 2);

        KitView view = kitService.view(session);
        assertThat(view.getLines()).hasSize(1);
        assertThat(view.getLines().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void view_capsQuantityToCurrentStockWhenReduced() {
        // Requested 5, but only 2 left on the shelf now.
        when(productRepository.findById(1)).thenReturn(Optional.of(product(1, 2)));
        kitService.add(session, 1, 5);

        KitView view = kitService.view(session);

        assertThat(view.getLines().get(0).getQuantity()).isEqualTo(2);
        assertThat(view.getLines().get(0).isQuantityReduced()).isTrue();
        assertThat(view.hasIssues()).isTrue();
    }

    @Test
    void view_dropsProductsThatBecameInactiveOrWereDeleted() {
        // Simulates a line that was valid when it was originally added, and
        // has since been deleted/deactivated - go straight through the
        // underlying Kit (bypassing add()'s own validation) to put the
        // session into the state an *older* kit would already be in, then
        // check that view() copes rather than crashing.
        kitService.getKit(session).add(1, 2);
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        KitView view = kitService.view(session);

        assertThat(view.getLines()).isEmpty();
        assertThat(view.getRemovedProductIds()).containsExactly(1);
        assertThat(view.hasIssues()).isTrue();
    }

    @Test
    void add_rejectsNonexistentProduct() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kitService.add(session, 99, 1))
                .isInstanceOf(ProductNotFoundException.class);
        assertThat(kitService.getKit(session).isEmpty()).isTrue();
    }

    @Test
    void add_rejectsInactiveProduct() {
        Product inactive = product(2, 10);
        inactive.setActive(false);
        when(productRepository.findById(2)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> kitService.add(session, 2, 1))
                .isInstanceOf(ProductNotFoundException.class);
        assertThat(kitService.getKit(session).isEmpty()).isTrue();
    }

    @Test
    void add_withZeroOrNegativeQuantity_isNoOpAndNeverTouchesTheRepository() {
        kitService.add(session, 1, 0);
        kitService.add(session, 1, -5);

        assertThat(kitService.getKit(session).isEmpty()).isTrue();
        verifyNoInteractions(productRepository);
    }

    @Test
    void add_clampsAnUnreasonablyLargeQuantity() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product(1, 10_000)));

        kitService.add(session, 1, 5_000_000);

        assertThat(kitService.getKit(session).getItems().get(1)).isEqualTo(Kit.MAX_QUANTITY_PER_LINE);
    }

    @Test
    void updateQuantityToZero_removesTheLine() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product(1, 10)));
        kitService.add(session, 1, 4);

        kitService.updateQuantity(session, 1, 0);

        assertThat(kitService.getKit(session).isEmpty()).isTrue();
    }

    @Test
    void updateQuantity_rejectsNonexistentProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product(1, 10)));
        kitService.add(session, 1, 1);
        when(productRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kitService.updateQuantity(session, 5, 3))
                .isInstanceOf(ProductNotFoundException.class);
        // The existing line is untouched, and no new line was smuggled in via update.
        assertThat(kitService.getKit(session).getItems()).containsOnlyKeys(1);
    }

    @Test
    void clear_emptiesTheKit() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product(1, 10)));
        kitService.add(session, 1, 2);
        kitService.clear(session);

        assertThat(kitService.getKit(session).isEmpty()).isTrue();
        assertThat(kitService.totalUnitCount(session)).isZero();
    }
}
