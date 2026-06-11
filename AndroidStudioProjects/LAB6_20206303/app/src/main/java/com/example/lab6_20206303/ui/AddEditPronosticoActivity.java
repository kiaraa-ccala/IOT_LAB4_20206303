package com.example.lab6_20206303.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_20206303.databinding.ActivityAddEditPronosticoBinding;
import com.example.lab6_20206303.model.Pronostico;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddEditPronosticoActivity extends AppCompatActivity {

    private ActivityAddEditPronosticoBinding binding;
    private DatabaseReference mDatabase;
    private Pronostico pronosticoToEdit;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditPronosticoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference("pronosticos");

        if (getIntent().hasExtra("pronostico")) {
            pronosticoToEdit = (Pronostico) getIntent().getSerializableExtra("pronostico");
            isEditMode = true;
            setupEditMode();
        }

        binding.etFecha.setOnClickListener(v -> showDatePicker());
        binding.btnSave.setOnClickListener(v -> savePronostico());
    }

    private void setupEditMode() {
        binding.etSeleccionA.setText(pronosticoToEdit.getSeleccionA());
        binding.etSeleccionB.setText(pronosticoToEdit.getSeleccionB());
        binding.etFecha.setText(pronosticoToEdit.getFechaPartido());
        binding.etGolesA.setText(String.valueOf(pronosticoToEdit.getGolesA()));
        binding.etGolesB.setText(String.valueOf(pronosticoToEdit.getGolesB()));

        // Mostrar selector de estado en modo edición
        binding.tilEstado.setVisibility(View.VISIBLE);
        binding.tvInfoEstado.setVisibility(View.GONE);

        String[] estados = {"Pendiente", "Acertado", "Fallado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);
        binding.spinnerEstado.setAdapter(adapter);
        binding.spinnerEstado.setText(pronosticoToEdit.getEstado(), false);

        if (!"Pendiente".equals(pronosticoToEdit.getEstado())) {
            lockFields();
        }
    }

    private void lockFields() {
        binding.etSeleccionA.setEnabled(false);
        binding.etSeleccionB.setEnabled(false);
        binding.etFecha.setEnabled(false);
        binding.etGolesA.setEnabled(false);
        binding.etGolesB.setEnabled(false);
        binding.spinnerEstado.setEnabled(false);
        binding.btnSave.setVisibility(View.GONE);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = year1 + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
                    binding.etFecha.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void savePronostico() {
        String selA = binding.etSeleccionA.getText().toString().trim();
        String selB = binding.etSeleccionB.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String gAStr = binding.etGolesA.getText().toString().trim();
        String gBStr = binding.etGolesB.getText().toString().trim();

        if (selA.isEmpty() || selB.isEmpty() || fecha.isEmpty() || gAStr.isEmpty() || gBStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selA.equalsIgnoreCase(selB)) {
            Toast.makeText(this, "Las selecciones no pueden ser iguales", Toast.LENGTH_SHORT).show();
            return;
        }

        int golesA = Integer.parseInt(gAStr);
        int golesB = Integer.parseInt(gBStr);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (isEditMode) {
            pronosticoToEdit.setSeleccionA(selA);
            pronosticoToEdit.setSeleccionB(selB);
            pronosticoToEdit.setFechaPartido(fecha);
            pronosticoToEdit.setGolesA(golesA);
            pronosticoToEdit.setGolesB(golesB);
            // CORRECCIÓN: Usar getText() para AutoCompleteTextView
            pronosticoToEdit.setEstado(binding.spinnerEstado.getText().toString());

            mDatabase.child(pronosticoToEdit.getId()).setValue(pronosticoToEdit)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Pronóstico actualizado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            String id = mDatabase.push().getKey();
            Pronostico nuevo = new Pronostico(id, selA, selB, fecha, golesA, golesB, "Pendiente", userId);
            mDatabase.child(id).setValue(nuevo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Pronóstico registrado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
