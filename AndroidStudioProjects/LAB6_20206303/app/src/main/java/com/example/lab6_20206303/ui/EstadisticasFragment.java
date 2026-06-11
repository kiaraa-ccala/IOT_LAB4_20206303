package com.example.lab6_20206303.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab6_20206303.databinding.FragmentEstadisticasBinding;
import com.example.lab6_20206303.model.Pronostico;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasFragment extends Fragment {

    private FragmentEstadisticasBinding binding;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("pronosticos");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                int acertados = 0;
                int fallados = 0;
                int pendientes = 0;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Pronostico p = postSnapshot.getValue(Pronostico.class);
                    if (p != null) {
                        total++;
                        switch (p.getEstado()) {
                            case "Acertado": acertados++; break;
                            case "Fallado": fallados++; break;
                            case "Pendiente": pendientes++; break;
                        }
                    }
                }

                updateChart(acertados, fallados, pendientes);
                binding.tvTotalCount.setText(String.valueOf(total));
                binding.tvAcertadoCount.setText(String.valueOf(acertados));
                binding.tvFalladoCount.setText(String.valueOf(fallados));
                binding.tvPendienteCount.setText(String.valueOf(pendientes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateChart(int acertados, int fallados, int pendientes) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if (acertados > 0) {
            entries.add(new PieEntry(acertados, "Acertados"));
            colors.add(Color.parseColor("#4CAF50")); // Verde
        }
        if (fallados > 0) {
            entries.add(new PieEntry(fallados, "Fallados"));
            colors.add(Color.parseColor("#F44336")); // Rojo
        }
        if (pendientes > 0) {
            entries.add(new PieEntry(pendientes, "Pendientes"));
            colors.add(Color.parseColor("#FFC107")); // Amarillo
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(16f);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.getLegend().setEnabled(true);
        binding.pieChart.setHoleRadius(40f);
        binding.pieChart.setTransparentCircleRadius(45f);
        binding.pieChart.setDrawEntryLabels(false);
        binding.pieChart.animateY(1000);
        binding.pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
