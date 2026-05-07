package com.example.lab4_20206303.api;

import com.example.lab4_20206303.model.CategoryResponse;
import com.example.lab4_20206303.model.MealResponse;
import com.example.lab4_20206303.model.RecipeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMealDbApi {

    @GET("categories.php")
    Call<CategoryResponse> getCategories();

    @GET("filter.php")
    Call<MealResponse> getMealsByCategory(@Query("c") String category);

    @GET("filter.php")
    Call<MealResponse> getMealsByIngredient(@Query("i") String ingredient);

    @GET("lookup.php")
    Call<RecipeResponse> getRecipeById(@Query("i") String mealId);

    @GET("random.php")
    Call<RecipeResponse> getRandomRecipe();
}
