package com.example.lab4_20206303.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4_20206303.R;
import com.example.lab4_20206303.api.ApiClient;
import com.example.lab4_20206303.model.Meal;
import com.example.lab4_20206303.model.MealResponse;
import com.example.lab4_20206303.model.Recipe;
import com.example.lab4_20206303.model.RecipeResponse;
import com.example.lab4_20206303.ui.adapter.MealAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealsFragment extends Fragment implements SensorEventListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TextView categoryLabel;
    private TextInputLayout ingredientLayout;
    private TextInputEditText ingredientInput;
    private Button searchButton;
    private MealAdapter adapter;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isFirstSensor = true;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastShakeTime;
    private boolean isFetchingRandom;
    private boolean isSearchMode;

    public MealsFragment() {
        super(R.layout.fragment_meals);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_meals);
        progressBar = view.findViewById(R.id.progress_meals);
        emptyView = view.findViewById(R.id.text_empty_meals);
        categoryLabel = view.findViewById(R.id.text_category_label);
        ingredientLayout = view.findViewById(R.id.input_ingredient_layout);
        ingredientInput = view.findViewById(R.id.edit_ingredient);
        searchButton = view.findViewById(R.id.button_search);

        adapter = new MealAdapter(this::onMealSelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        String category = getArguments() != null ? getArguments().getString("category") : "";
        if (category != null && !category.isEmpty()) {
            isSearchMode = false;
            categoryLabel.setVisibility(View.VISIBLE);
            categoryLabel.setText(getString(R.string.category_label) + " " + category);
            ingredientLayout.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
            loadMealsByCategory(category);
        } else {
            isSearchMode = true;
            categoryLabel.setVisibility(View.GONE);
            ingredientLayout.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
        }

        searchButton.setOnClickListener(view1 -> {
            String ingredient = ingredientInput.getText() != null
                    ? ingredientInput.getText().toString().trim() : "";
            if (ingredient.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa un ingrediente", Toast.LENGTH_SHORT).show();
                return;
            }
            loadMealsByIngredient(ingredient);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSearchMode && sensorManager != null && accelerometer != null) {
            isFirstSensor = true;
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null && isSearchMode) {
            sensorManager.unregisterListener(this);
        }
    }

    private void loadMealsByCategory(String category) {
        showLoading(true);
        ApiClient.getApi().getMealsByCategory(category).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    updateMeals(response.body().getMeals());
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                showError();
            }
        });
    }

    private void loadMealsByIngredient(String ingredient) {
        showLoading(true);
        ApiClient.getApi().getMealsByIngredient(ingredient).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call,
                                   @NonNull Response<MealResponse> response) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    updateMeals(response.body().getMeals());
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                showError();
            }
        });
    }

    private void updateMeals(List<Meal> meals) {
        adapter.submitList(meals);
        emptyView.setVisibility(meals == null || meals.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void onMealSelected(Meal meal) {
        Bundle args = new Bundle();
        args.putString("mealId", meal.getIdMeal());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_meals_to_recipe, args);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (loading) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showError() {
        Toast.makeText(requireContext(), "Error al cargar platos", Toast.LENGTH_SHORT).show();
    }

    private void fetchRandomRecipe() {
        isFetchingRandom = true;
        ApiClient.getApi().getRandomRecipe().enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call,
                                   @NonNull Response<RecipeResponse> response) {
                isFetchingRandom = false;
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getMeals();
                    if (recipes != null && !recipes.isEmpty()) {
                        String mealId = recipes.get(0).getIdMeal();
                        Toast.makeText(requireContext(),
                                getString(R.string.random_recipe), Toast.LENGTH_SHORT).show();
                        Bundle args = new Bundle();
                        args.putString("mealId", mealId);
                        NavHostFragment.findNavController(MealsFragment.this)
                                .navigate(R.id.action_meals_to_recipe, args);
                        return;
                    }
                }
                showError();
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                isFetchingRandom = false;
                if (!isAdded()) {
                    return;
                }
                showError();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isSearchMode) {
            return;
        }
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (isFirstSensor) {
            lastX = x;
            lastY = y;
            lastZ = z;
            isFirstSensor = false;
            return;
        }
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        float acceleration = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        long now = System.currentTimeMillis();
        if (acceleration > 4.0f && now - lastShakeTime > 1500 && !isFetchingRandom) {
            lastShakeTime = now;
            fetchRandomRecipe();
        }

        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No-op
    }
}
