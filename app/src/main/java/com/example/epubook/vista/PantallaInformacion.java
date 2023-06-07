package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.epubook.R;

public class PantallaInformacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.activity_pantalla_informacion);
    }
}