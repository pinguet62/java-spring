package fr.pinguet62.test.springdataencrypt;

import javax.persistence.AttributeConverter;

public class CryptoConverter implements AttributeConverter<String, String> {

    public CryptoConverter() {
        System.out.println("CryptoConverter::new");
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return new StringBuilder(attribute).reverse().toString();
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return new StringBuilder(dbData).reverse().toString();
    }

}
