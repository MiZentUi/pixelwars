package com.mizentui.pixelwars.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String color;

    public String getColor() {
        return color;
    }

}
