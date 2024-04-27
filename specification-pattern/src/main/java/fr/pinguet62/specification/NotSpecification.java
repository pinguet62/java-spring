package fr.pinguet62.specification;

public class NotSpecification<T> extends AbstractCompositeSpecification<T> {

    public NotSpecification(Specification<T> specification) {
        super(specification);
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return !specifications[0].isSatisfiedBy(candidate);
    }
}
