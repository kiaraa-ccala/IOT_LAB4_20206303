package com.example.lab4_20206303.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lab4_20206303.R;
import com.example.lab4_20206303.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    private final List<Meal> items = new ArrayList<>();
    private final OnMealClickListener listener;

    public MealAdapter(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Meal> meals) {
        items.clear();
        if (meals != null) {
            items.addAll(meals);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView nameView;
        private final TextView idView;
        private Meal current;

        MealViewHolder(@NonNull View itemView, OnMealClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_meal);
            nameView = itemView.findViewById(R.id.text_meal_name);
            idView = itemView.findViewById(R.id.text_meal_id);
            itemView.setOnClickListener(view -> {
                if (current != null) {
                    listener.onMealClick(current);
                }
            });
        }

        void bind(Meal meal) {
            current = meal;
            nameView.setText(meal.getStrMeal());
            idView.setText("ID: " + meal.getIdMeal());
            Glide.with(itemView)
                    .load(meal.getStrMealThumb())
                    .centerCrop()
                    .into(imageView);
        }
    }
}
