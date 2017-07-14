package fr.pinguet62.test.pattern.specification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.pinguet62.test.pattern.specification.model.Product;
import fr.pinguet62.test.pattern.specification.rule.HighPrice;
import fr.pinguet62.test.pattern.specification.rule.IsBlack;

public class SpecificationTest {

    private final Specification<Product> isBlack = new IsBlack();

    private final Specification<Product> more10 = new HighPrice();

    @Test
    public void test1() {
        Product product = new Product("water", 1.23, "white");
        assertFalse(isBlack.and(more10).isSatisfiedBy(product));
    }

    @Test
    public void test2() {
        Product product = new Product("car", 12_345, "black");
        assertTrue(isBlack.and(more10).isSatisfiedBy(product));
    }

}