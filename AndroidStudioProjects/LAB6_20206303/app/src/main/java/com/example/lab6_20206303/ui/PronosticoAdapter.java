package com.example.lab6_20206303.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20206303.databinding.ItemPronosticoBinding;
import com.example.lab6_20206303.model.Pronostico;

import java.util.List;

public class PronosticoAdapter extends RecyclerView.Adapter<PronosticoAdapter.ViewHolder> {

    private List<Pronostico> pronosticos;
    private OnPronosticoClickListener listener;

    public interface OnPronosticoClickListener {
        void onEdit(Pronostico pronostico);
        void onDelete(Pronostico pronostico);
    }

    public PronosticoAdapter(List<Pronostico> pronosticos, OnPronosticoClickListener listener) {
        this.pronosticos = pronosticos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPronosticoBinding binding = ItemPronosticoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pronostico p = pronosticos.get(position);
        holder.binding.textViewPartida.setText(p.getSeleccionA() + " vs " + p.getSeleccionB());
        holder.binding.textViewFecha.setText("Fecha: " + p.getFechaPartido());
        holder.binding.textViewResultado.setText(p.getGolesA() + " - " + p.getGolesB());
        holder.binding.textViewEstado.setText(p.getEstado());

        // Colores para los estados
        int color;
        switch (p.getEstado()) {
            case "Acertado":
                color = Color.parseColor("#4CAF50"); // Verde
                break;
            case "Fallado":
                color = Color.parseColor("#F44336"); // Rojo
                break;
            default:
                color = Color.parseColor("#FFC107"); // Ámbar/Amarillo para Pendiente
                break;
        }
        ((com.google.android.material.card.MaterialCardView) holder.binding.textViewEstado.getParent()).setCardBackgroundColor(color);

        holder.binding.btnEdit.setOnClickListener(v -> listener.onEdit(p));
        holder.binding.btnDelete.setOnClickListener(v -> listener.onDelete(p));

        if (!"Pendiente".equals(p.getEstado())) {
            holder.binding.btnEdit.setVisibility(View.GONE);
            holder.binding.btnDelete.setVisibility(View.GONE);
        } else {
            holder.binding.btnEdit.setVisibility(View.VISIBLE);
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return pronosticos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPronosticoBinding binding;
        public ViewHolder(ItemPronosticoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
