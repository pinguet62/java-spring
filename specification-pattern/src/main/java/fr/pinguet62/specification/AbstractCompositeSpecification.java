package fr.pinguet62.specification;

public abstract class AbstractCompositeSpecification<T> implements Specification<T> {

    protected final Specification<T>[] specifications;

    public AbstractCompositeSpecification(Specification<T>... specifications) {
        this.specifications = specifications;
    }
}
