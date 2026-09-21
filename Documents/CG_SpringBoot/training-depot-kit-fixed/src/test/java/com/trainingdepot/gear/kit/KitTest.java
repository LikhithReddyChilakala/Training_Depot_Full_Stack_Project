package com.trainingdepot.gear.kit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Kit has no collaborators (no Spring, no repository), so these are plain
 * unit tests of the productId -> quantity bookkeeping itself, independent
 * of anything KitService adds on top.
 */
class KitTest {

    private Kit kit;

    @BeforeEach
    void setUp() {
        kit = new Kit();
    }

    @Test
    void newKit_isEmpty() {
        assertThat(kit.isEmpty()).isTrue();
        assertThat(kit.totalUnitCount()).isZero();
    }

    @Test
    void add_addsANewLine() {
        kit.add(1, 2);

        assertThat(kit.isEmpty()).isFalse();
        assertThat(kit.getItems()).containsEntry(1, 2);
        assertThat(kit.totalUnitCount()).isEqualTo(2);
    }

    @Test
    void add_sameProductTwice_incrementsRatherThanDuplicating() {
        kit.add(1, 1);
        kit.add(1, 2);

        assertThat(kit.getItems()).hasSize(1).containsEntry(1, 3);
    }

    @Test
    void add_withZeroOrNegativeQuantity_isNoOp() {
        kit.add(1, 0);
        kit.add(1, -3);

        assertThat(kit.isEmpty()).isTrue();
    }

    @Test
    void add_clampsASingleHugeQuantityToTheMax() {
        kit.add(1, 50_000);

        assertThat(kit.getItems().get(1)).isEqualTo(Kit.MAX_QUANTITY_PER_LINE);
    }

    @Test
    void add_clampsACumulativeQuantityToTheMax() {
        kit.add(1, 600);
        kit.add(1, 600);

        // 600 + 600 would be 1200, which is above the cap.
        assertThat(kit.getItems().get(1)).isEqualTo(Kit.MAX_QUANTITY_PER_LINE);
    }

    @Test
    void setQuantity_updatesAnExistingLine() {
        kit.add(1, 1);

        kit.setQuantity(1, 5);

        assertThat(kit.getItems()).containsEntry(1, 5);
    }

    @Test
    void setQuantity_zeroOrLess_removesTheLine() {
        kit.add(1, 1);

        kit.setQuantity(1, 0);

        assertThat(kit.isEmpty()).isTrue();
    }

    @Test
    void remove_dropsTheLine() {
        kit.add(1, 1);
        kit.add(2, 1);

        kit.remove(1);

        assertThat(kit.getItems()).containsOnlyKeys(2);
    }

    @Test
    void remove_ofAbsentProduct_isHarmlessNoOp() {
        kit.remove(404);

        assertThat(kit.isEmpty()).isTrue();
    }

    @Test
    void clear_emptiesEveryLine() {
        kit.add(1, 1);
        kit.add(2, 3);

        kit.clear();

        assertThat(kit.isEmpty()).isTrue();
        assertThat(kit.totalUnitCount()).isZero();
    }

    @Test
    void getItems_returnsADefensiveCopy() {
        kit.add(1, 1);

        kit.getItems().put(2, 99);

        // Mutating the returned map must never leak back into the kit.
        assertThat(kit.getItems()).containsOnlyKeys(1);
    }

    @Test
    void totalUnitCount_sumsAcrossAllLines() {
        kit.add(1, 2);
        kit.add(2, 5);

        assertThat(kit.totalUnitCount()).isEqualTo(7);
    }
}
