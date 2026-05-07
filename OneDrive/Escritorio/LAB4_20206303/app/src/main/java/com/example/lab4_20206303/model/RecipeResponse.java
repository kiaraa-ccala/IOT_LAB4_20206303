package com.example.lab4_20206303.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeResponse {

    @SerializedName("meals")
    private List<Recipe> meals;

    public List<Recipe> getMeals() {
        return meals;
    }
}
