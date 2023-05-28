package com.example.epubook.vista;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlEscribir;
import com.example.epubook.modelo.Libro;
import com.example.epubook.modelo.LibroEscribir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PantallaCrear extends AppCompatActivity {

    private EditText titulo, autor;
    private Button cargarportada, botonCrear;

    private String imagenSeleccionada;

    private ImageView imagenPortada;
    private ControlEscribir controlEscribir = new ControlEscribir(PantallaCrear.this);
    private static final int elegir_imagen = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_crear);

        titulo = findViewById(R.id.tituloCrear);
        autor = findViewById(R.id.autorCrear);
        botonCrear = findViewById(R.id.btCrear);
        cargarportada = findViewById(R.id.cargarPortada);
        imagenPortada = findViewById(R.id.imgPortada);

        cargarportada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), elegir_imagen);
            }
        });

        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(titulo.getText().toString().isEmpty()){
                    titulo.setError("Pon un título");
                    titulo.requestFocus();
                }else if(autor.getText().toString().isEmpty()){
                    autor.setError("Pon un autor");
                    autor.requestFocus();
                }else{
                    if(imagenSeleccionada!=null){
                        controlEscribir.crearLibro(titulo.getText().toString(), autor.getText().toString(), imagenSeleccionada);
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == elegir_imagen && resultCode == RESULT_OK && data != null){
            Uri imagenUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
                imagenPortada.setImageBitmap(bitmap);
                imagenSeleccionada = obtenerRuta(imagenUri);
                //botonCrear.setEnabled(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String obtenerRuta(Uri imagenUri) {

        Cursor cursor = getContentResolver().query(imagenUri, null, null, null);
        cursor.moveToFirst();
        int ruta = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(ruta);

    }
}