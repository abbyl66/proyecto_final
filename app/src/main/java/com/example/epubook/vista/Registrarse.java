package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.modelo.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrarse extends AppCompatActivity {

    EditText regNombre, regEmail, regUser, regContr;
    TextView regIniciar;
    Button botonReg;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        regNombre = findViewById(R.id.reg_nombre);
        regEmail = findViewById(R.id.reg_email);
        regUser = findViewById(R.id.reg_user);
        regContr = findViewById(R.id.reg_ctrsenia);
        botonReg = findViewById(R.id.btreg_regist);
        regIniciar = findViewById(R.id.btreg_iniciar);

        botonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String nombre = regNombre.getText().toString();
                String email = regEmail.getText().toString();
                String usuario = regUser.getText().toString();
                String contrasenia = regContr.getText().toString();


                if(nombre.isEmpty() || email.isEmpty() || usuario.isEmpty() || contrasenia.isEmpty() ){
                    Toast.makeText(Registrarse.this, "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show();
                }else{

                    Usuario nuevoUsuario = new Usuario(nombre, email, usuario, contrasenia);
                    reference.child(nombre).setValue(nuevoUsuario);

                    Toast.makeText(Registrarse.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registrarse.this, InicioSesion.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

        regIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registrarse.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

    }
}