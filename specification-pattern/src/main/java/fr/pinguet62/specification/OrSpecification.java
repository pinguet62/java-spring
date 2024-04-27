package fr.pinguet62.specification;

public class OrSpecification<T> extends AbstractCompositeSpecification<T> {

    public OrSpecification(Specification<T>... specifications) {
        super(specifications);
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        boolean result = false;
        for (Specification<T> specification : specifications)
            result |= specification.isSatisfiedBy(candidate);
        return result;
    }
}
