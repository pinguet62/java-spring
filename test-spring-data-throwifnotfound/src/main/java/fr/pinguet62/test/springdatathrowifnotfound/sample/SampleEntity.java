package fr.pinguet62.test.springdatathrowifnotfound.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleEntity {

    @Id
    Integer id;

    String name;
}
