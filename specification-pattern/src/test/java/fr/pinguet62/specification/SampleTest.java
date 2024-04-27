package fr.pinguet62.specification;

import fr.pinguet62.specification.sample.model.Product;
import fr.pinguet62.specification.sample.rule.IsBlack;
import fr.pinguet62.specification.sample.rule.HighPrice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SampleTest {

    final Specification<Product> isBlack = new IsBlack();

    final Specification<Product> more10 = new HighPrice();

    @Test
    void test1() {
        Product product = new Product("water", 1.23, "white");
        assertFalse(isBlack.and(more10).isSatisfiedBy(product));
    }

    @Test
    void test2() {
        Product product = new Product("car", 12_345, "black");
        assertTrue(isBlack.and(more10).isSatisfiedBy(product));
    }
}
