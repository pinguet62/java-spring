package fr.pinguet62.test.springdatathrowifnotfound.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleEntity {

    @Id
    Integer id;

    String name;
}
