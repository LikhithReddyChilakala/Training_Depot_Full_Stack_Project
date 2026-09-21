package com.trainingdepot.gear.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trainingdepot.gear.exception.ProductNotFoundException;
import com.trainingdepot.gear.model.EquipmentCategory;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.repository.OrderItemRepository;
import com.trainingdepot.gear.repository.ProductRepository;
import com.trainingdepot.gear.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ImageStorageService imageStorageService;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, orderItemRepository, imageStorageService);
    }

    private Product activeProduct(int id) {
        Product p = new Product();
        p.setId(id);
        p.setName("5 KG Rubber Dumbbell");
        p.setPrice(new BigDecimal("999"));
        p.setCategory(EquipmentCategory.DUMBBELLS);
        p.setStockQuantity(10);
        p.setActive(true);
        return p;
    }

    @Test
    void save_rebuildsSpecificationMapFromParallelFormArrays() {
        Product product = activeProduct(1);
        product.setSpecKeys(List.of("Weight", "Material"));
        product.setSpecValues(List.of("05 KG", "Cast Iron"));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product saved = productService.save(product);

        assertThat(saved.getSpecifications())
                .containsEntry("Weight", "05 KG")
                .containsEntry("Material", "Cast Iron");
    }

    @Test
    void getStorefrontProduct_throwsWhenInactive() {
        Product inactive = activeProduct(2);
        inactive.setActive(false);
        when(productRepository.findByIdWithSpecifications(2)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> productService.getStorefrontProduct(2))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getStorefrontProduct_throwsWhenMissing() {
        when(productRepository.findByIdWithSpecifications(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getStorefrontProduct(99))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void delete_softDeletesWhenOrderHistoryExists() {
        Product product = activeProduct(3);
        when(productRepository.findByIdWithSpecifications(3)).thenReturn(Optional.of(product));
        when(orderItemRepository.existsByProduct_Id(3)).thenReturn(true);

        productService.delete(3);

        assertThat(product.isActive()).isFalse();
        verify(productRepository).save(product);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void delete_hardDeletesWhenNoOrderHistory() {
        Product product = activeProduct(4);
        when(productRepository.findByIdWithSpecifications(4)).thenReturn(Optional.of(product));
        when(orderItemRepository.existsByProduct_Id(4)).thenReturn(false);

        productService.delete(4);

        verify(productRepository).delete(product);
        verify(productRepository, never()).save(any());
    }

    @Test
    void getStorefrontProducts_capsAtThreeAndSkipsInactive() {
        Product a = activeProduct(1);
        Product b = activeProduct(2);
        b.setActive(false);
        Product c = activeProduct(3);
        Product d = activeProduct(4);

        when(productRepository.findByIdWithSpecifications(1)).thenReturn(Optional.of(a));
        when(productRepository.findByIdWithSpecifications(2)).thenReturn(Optional.of(b));
        when(productRepository.findByIdWithSpecifications(3)).thenReturn(Optional.of(c));
        when(productRepository.findByIdWithSpecifications(4)).thenReturn(Optional.of(d));

        List<Product> result = productService.getStorefrontProducts(List.of(1, 2, 3, 4));

        assertThat(result).extracting(Product::getId).containsExactly(1, 3, 4);
    }
}
