package fr.pinguet62.specification;

public interface Specification<T> {

    boolean isSatisfiedBy(T candidate);

    default Specification<T> and(Specification<T> specification) {
        return new AndSpecification<>(this, specification);
    }

    default Specification<T> not() {
        return new NotSpecification<>(this);
    }

    default Specification<T> or(Specification<T> specification) {
        return new OrSpecification<>(this, specification);
    }
}
