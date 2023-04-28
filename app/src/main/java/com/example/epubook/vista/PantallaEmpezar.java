package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.epubook.R;

public class PantallaEmpezar extends AppCompatActivity {

    Button botonIniciar;
    TextView botonRegist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_empezar);

        botonIniciar = findViewById(R.id.bt_iniciar);
        botonRegist = findViewById(R.id.bt_registrate);

        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaEmpezar.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

        botonRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaEmpezar.this, Registrarse.class);
                startActivity(intent);
                finish();
            }
        });
    }
}