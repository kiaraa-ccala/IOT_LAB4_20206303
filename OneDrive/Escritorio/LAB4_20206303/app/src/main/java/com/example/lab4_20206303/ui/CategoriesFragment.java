package com.example.lab4_20206303.ui;

import android.os.Bundle;
import android.view.View;
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
import com.example.lab4_20206303.model.Category;
import com.example.lab4_20206303.model.CategoryResponse;
import com.example.lab4_20206303.ui.adapter.CategoryAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private CategoryAdapter adapter;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_categories);
        progressBar = view.findViewById(R.id.progress_categories);
        emptyView = view.findViewById(R.id.text_empty_categories);

        adapter = new CategoryAdapter(this::onCategorySelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        loadCategories();
    }

    private void loadCategories() {
        showLoading(true);
        ApiClient.getApi().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call,
                                   @NonNull Response<CategoryResponse> response) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getCategories();
                    adapter.submitList(categories);
                    emptyView.setVisibility(categories == null || categories.isEmpty()
                            ? View.VISIBLE : View.GONE);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    return;
                }
                showLoading(false);
                showError();
            }
        });
    }

    private void onCategorySelected(Category category) {
        Bundle args = new Bundle();
        args.putString("category", category.getStrCategory());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_categories_to_meals, args);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (loading) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showError() {
        Toast.makeText(requireContext(), "Error al cargar categorias", Toast.LENGTH_SHORT).show();
    }
}
