package com.example.lab4_20206303;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button ingresarButton;
    private AlertDialog noConnectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ingresarButton = findViewById(R.id.button_ingresar);
        ingresarButton.setOnClickListener(view -> {
            if (hasInternetConnection()) {
                startActivity(new Intent(MainActivity.this, AppActivity.class));
            } else {
                showNoConnectionDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateConnectivityState();
    }

    private void updateConnectivityState() {
        boolean connected = hasInternetConnection();
        ingresarButton.setEnabled(connected);
        if (!connected) {
            showNoConnectionDialog();
        }
    }

    private boolean hasInternetConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    private void showNoConnectionDialog() {
        if (noConnectionDialog != null && noConnectionDialog.isShowing()) {
            return;
        }
        noConnectionDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.no_connection_title)
                .setMessage(R.string.no_connection_message)
                .setPositiveButton(R.string.settings, (dialog, which) ->
                    startActivity(new Intent(Settings.ACTION_SETTINGS)))
                .setNegativeButton(R.string.cancel, null)
                .create();
        noConnectionDialog.show();
    }
}