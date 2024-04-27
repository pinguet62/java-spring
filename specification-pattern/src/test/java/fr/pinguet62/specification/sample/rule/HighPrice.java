package fr.pinguet62.specification.sample.rule;

import fr.pinguet62.specification.Specification;
import fr.pinguet62.specification.sample.model.Product;

public class HighPrice implements Specification<Product> {

    @Override
    public boolean isSatisfiedBy(Product product) {
        return product.getPrice() >= 100;
    }
}
