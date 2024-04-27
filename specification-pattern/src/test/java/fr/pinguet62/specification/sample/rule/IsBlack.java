package fr.pinguet62.specification.sample.rule;

import fr.pinguet62.specification.sample.model.Product;
import fr.pinguet62.specification.Specification;

public class IsBlack implements Specification<Product> {

    @Override
    public boolean isSatisfiedBy(Product product) {
        return product.getColor().equals("black");
    }
}
