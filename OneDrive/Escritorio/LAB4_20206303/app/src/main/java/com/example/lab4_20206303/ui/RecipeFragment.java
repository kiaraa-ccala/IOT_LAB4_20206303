package com.example.lab4_20206303.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lab4_20206303.R;
import com.example.lab4_20206303.api.ApiClient;
import com.example.lab4_20206303.model.Recipe;
import com.example.lab4_20206303.model.RecipeResponse;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeFragment extends Fragment {

    private TextInputEditText mealIdInput;
    private Button searchButton;
    private ProgressBar progressBar;
    private ImageView recipeImage;
    private TextView recipeName;
    private TextView recipeCategory;
    private TextView recipeArea;
    private TextView recipeIngredients;
    private TextView recipeInstructions;

    public RecipeFragment() {
        super(R.layout.fragment_recipe);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealIdInput = view.findViewById(R.id.edit_meal_id);
        searchButton = view.findViewById(R.id.button_recipe_search);
        progressBar = view.findViewById(R.id.progress_recipe);
        recipeImage = view.findViewById(R.id.image_recipe);
        recipeName = view.findViewById(R.id.text_recipe_name);
        recipeCategory = view.findViewById(R.id.text_recipe_category);
        recipeArea = view.findViewById(R.id.text_recipe_area);
        recipeIngredients = view.findViewById(R.id.text_recipe_ingredients);
        recipeInstructions = view.findViewById(R.id.text_recipe_instructions);

        searchButton.setOnClickListener(view1 -> {
            String mealId = mealIdInput.getText() != null
                    ? mealIdInput.getText().toString().trim() : "";
            if (mealId.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa un ID", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchRecipeById(mealId);
        });

        String mealIdArg = getArguments() != null ? getArguments().getString("mealId") : "";
        if (mealIdArg != null && !mealIdArg.isEmpty()) {
            mealIdInput.setText(mealIdArg);
            fetchRecipeById(mealIdArg);
        }
    }

    private void fetchRecipeById(String mealId) {
        showLoading(true);
        ApiClient.getApi().getRecipeById(mealId).enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call,
                                   @NonNull Response<RecipeResponse> response) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getMeals();
                    if (recipes != null && !recipes.isEmpty()) {
                        bindRecipe(recipes.get(0));
                        return;
                    }
                }
                showError();
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                showError();
            }
        });
    }

    private void bindRecipe(Recipe recipe) {
        recipeName.setText(recipe.getStrMeal());
        recipeCategory.setText(getString(R.string.recipe_category_label, recipe.getStrCategory()));
        recipeArea.setText(getString(R.string.recipe_area_label, recipe.getStrArea()));

        List<String> ingredients = recipe.buildIngredientLines();
        StringBuilder builder = new StringBuilder(getString(R.string.recipe_ingredients_title));
        for (String line : ingredients) {
            builder.append("\n- ").append(line);
        }
        recipeIngredients.setText(builder.toString());
        recipeInstructions.setText(recipe.getStrInstructions());

        Glide.with(this)
                .load(recipe.getStrMealThumb())
                .centerCrop()
                .into(recipeImage);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError() {
        Toast.makeText(requireContext(), "No se encontro la receta", Toast.LENGTH_SHORT).show();
    }
}
