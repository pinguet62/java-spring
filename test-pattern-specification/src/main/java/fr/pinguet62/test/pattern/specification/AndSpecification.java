package fr.pinguet62.test.pattern.specification;

public class AndSpecification<T> extends AbstractCompositeSpecification<T> {

    public AndSpecification(Specification<T>... specifications) {
        super(specifications);
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        boolean result = true;
        for (Specification<T> specification : specifications)
            result &= specification.isSatisfiedBy(candidate);
        return result;
    }
}
