package fr.pinguet62.springdata.throwifnotfound.sample;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleEntity {

    @Id
    Integer id;

    String name;
}
