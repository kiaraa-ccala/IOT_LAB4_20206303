package com.example.lab6_20206303.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab6_20206303.databinding.FragmentPronosticosBinding;
import com.example.lab6_20206303.model.Pronostico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PronosticosFragment extends Fragment implements PronosticoAdapter.OnPronosticoClickListener {

    private FragmentPronosticosBinding binding;
    private DatabaseReference mDatabase;
    private PronosticoAdapter adapter;
    private List<Pronostico> pronosticosList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPronosticosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("pronosticos");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new PronosticoAdapter(pronosticosList, this);
        binding.recyclerViewPronosticos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPronosticos.setAdapter(adapter);

        binding.fabAddPronostico.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddEditPronosticoActivity.class));
        });

        mDatabase.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pronosticosList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Pronostico p = postSnapshot.getValue(Pronostico.class);
                    pronosticosList.add(p);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(Pronostico pronostico) {
        if (!"Pendiente".equals(pronostico.getEstado())) {
            Toast.makeText(getContext(), "Solo se pueden editar pronósticos pendientes", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), AddEditPronosticoActivity.class);
        intent.putExtra("pronostico", pronostico);
        startActivity(intent);
    }

    @Override
    public void onDelete(Pronostico pronostico) {
        if (!"Pendiente".equals(pronostico.getEstado())) {
            Toast.makeText(getContext(), "Solo se pueden eliminar pronósticos pendientes", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este pronóstico?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    mDatabase.child(pronostico.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Pronóstico eliminado", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
