package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditarPerfil extends AppCompatActivity {

    private EditText nombre, usuario, email, contrasenia;
    private Button cancelar, guardarCambios;
    private String nombreUser, usuarioUser, emailUser, contrUser;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String uid;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        nombre = findViewById(R.id.editNombre);
        usuario = findViewById(R.id.editUser);
        email = findViewById(R.id.editEmail);
        contrasenia = findViewById(R.id.editCtrsenia);

        cancelar = findViewById(R.id.btedit_cancelar);
        guardarCambios = findViewById(R.id.bteditConfirmar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(uid);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mostrarDatos();

        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDatos();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() { //Dialogo
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void mostrarDatos(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);

                nombreUser = user.getNombre();
                usuarioUser = user.getUser();
                emailUser = user.getEmail();
                contrUser = user.getCtrsenia();

                nombre.setText(nombreUser);
                usuario.setText(usuarioUser);
                email.setText(emailUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void guardarDatos(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);

                nombreUser = user.getNombre();
                usuarioUser = user.getUser();
                emailUser = user.getEmail();
                contrUser = user.getCtrsenia();

                if(contrUser.equals(contrasenia.getText().toString())){

                    if(nombre.getText().toString().isEmpty() || usuario.getText().toString().isEmpty() || email.getText().toString().isEmpty()){
                        Toast.makeText(EditarPerfil.this, "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show();
                    }else{
                        if(emailUser.equals(email.getText().toString())){
                            comprobarDatos();
                        }else{

                            databaseReference.orderByChild("email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        email.setError("El email ya está registrado.");
                                        email.requestFocus();
                                    }else{
                                        comprobarDatos();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                }else if(contrasenia.getText().toString().isEmpty()){
                    contrasenia.setError("Debe introducir la contraseña");
                    contrasenia.requestFocus();
                }else if (!contrUser.equals(contrasenia.getText().toString())){
                    contrasenia.setError("Contraseña incorrecta.");
                    contrasenia.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void comprobarDatos(){
        if(usuarioUser.equals(usuario.getText().toString())){
            if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){

                reference.child("nombre").setValue(nombre.getText().toString());
                reference.child("user").setValue(usuario.getText().toString());
                reference.child("email").setValue(email.getText().toString());

                Toast.makeText(EditarPerfil.this, "Cambios guardados.", Toast.LENGTH_SHORT).show();
                finish();

            }else{
                email.setError("Formato incorrecto.");
                email.requestFocus();
            }
        }else{
            databaseReference.orderByChild("user").equalTo(usuario.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        usuario.setError("El usuario ya existe.");
                        usuario.requestFocus();
                    }else{
                        if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){

                            reference.child("nombre").setValue(nombre.getText().toString());
                            reference.child("user").setValue(usuario.getText().toString());
                            reference.child("email").setValue(email.getText().toString());

                            Toast.makeText(EditarPerfil.this, "Cambios guardados.", Toast.LENGTH_SHORT).show();
                            finish();

                        }else{
                            email.setError("Formato incorrecto.");
                            email.requestFocus();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}