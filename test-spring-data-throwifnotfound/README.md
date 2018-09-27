# Spring Data: *throw if not found*

https://stackoverflow.com/questions/26258158/how-to-instrument-advice-a-spring-data-jpa-repository

## Problem

```java
@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Integer> {
    SampleEntity findByName(String name);
}

SampleEntity entity1 = repository.findById(1).orElseThrow(() -> new NotFoundException());
SampleEntity entity2 = repository.findById(2).orElseThrow(() -> new NotFoundException());
SampleEntity entity3 = repository.findById(3).orElseThrow(() -> new NotFoundException());
// ...

SampleEntity entityA = repository.findByName("A"); // nullable
if (entity == null) throw new NotFoundException();
SampleEntity entityB = repository.findByName("B"); // nullable
if (entity == null) throw new NotFoundException();
SampleEntity entityC = repository.findByName("C"); // nullable
if (entity == null) throw new NotFoundException();
// ...
```

## Solution

```java
@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<T, ID> {
    SampleEntity findByIdOrThrow();
}
@Repository
public interface SampleRepository extends CustomJpaRepository<SampleEntity, Integer> {
    @ThrowIfNotFound
    SampleEntity findByName(String name);
}

SampleEntity entity1 = repository.findById(1); // != null
SampleEntity entity2 = repository.findById(2); // != null
SampleEntity entity3 = repository.findById(3); // != null
// ...

SampleEntity entityA = repository.findByName("A"); // != null
SampleEntity entityB = repository.findByName("B"); // != null
SampleEntity entityC = repository.findByName("C"); // != null
// ...
```
