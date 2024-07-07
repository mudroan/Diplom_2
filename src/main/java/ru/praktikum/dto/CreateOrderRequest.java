package ru.praktikum.dto;

import java.util.ArrayList;

public class CreateOrderRequest {

    private ArrayList<String> ingredients;

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
