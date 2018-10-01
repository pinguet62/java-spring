package fr.pinguet62.test.springdataencrypt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedEntity {

    @Id
    Integer id;

    @Convert(converter = CryptoConverter.class)
    String name;

}
