package fr.pinguet62.test.pattern.specification.api.rule;

import fr.pinguet62.test.pattern.specification.Specification;
import fr.pinguet62.test.pattern.specification.api.model.Product;

public class HighPrice implements Specification<Product> {

    @Override
    public boolean isSatisfiedBy(Product product) {
        return product.getPrice() >= 100;
    }
}
