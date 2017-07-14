package fr.pinguet62.test.pattern.specification.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Product {

    private final String name;

    private final double price;

    private final String color;

}