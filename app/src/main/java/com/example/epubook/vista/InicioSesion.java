package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.epubook.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class InicioSesion extends AppCompatActivity {

    EditText iniUsuario, iniContr;
    Button botonIni;
    TextView iniRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion);

        iniUsuario = findViewById(R.id.ini_user);
        iniContr = findViewById(R.id.ini_ctrsenia);
        botonIni = findViewById(R.id.btini_iniciar);
        iniRegistro = findViewById(R.id.btini_reg);

        botonIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!comprobarUsuario() | !comprobarContr()){

                }else{
                    validarUsuario();
                }
            }
        });

        iniRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioSesion.this, Registrarse.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public Boolean comprobarUsuario(){
        String usuarioIni = iniUsuario.getText().toString();
        if(usuarioIni.isEmpty()){
            iniUsuario.setError("Debe especificar el usuario.");
            return false;
        }else{
            iniUsuario.setError(null);
            return true;
        }
    }

    public Boolean comprobarContr(){
        String contrIni = iniContr.getText().toString();
        if(contrIni.isEmpty()){
            iniContr.setError("Debe especificar la contraseña.");
            return false;
        }else{
            iniContr.setError(null);
            return true;
        }
    }

    public void validarUsuario(){
        String vUsuario = iniUsuario.getText().toString().trim();
        String vContrasenia = iniContr.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query comprobarUserBD = reference.orderByChild("user").equalTo(vUsuario);

        comprobarUserBD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists()){
                    iniUsuario.setError(null);

                    //Obtiene la contrasenia del usuario especificado de la bd.
                    String comprobarContrBD = snapshot.child(vUsuario).child("ctrsenia").getValue(String.class);

                    //Iguala la contrasenia introducida por teclado y el que está en la bd.
                    if(Objects.equals(comprobarContrBD, vContrasenia)){
                        iniUsuario.setError(null);
                        Intent intent = new Intent(InicioSesion.this, PantallaInicio.class);
                        startActivity(intent);
                        finish();
                    }else{
                        iniContr.setError("Contraseña incorrecta.");
                        iniContr.requestFocus();
                    }
                }else{

                    iniUsuario.setError("Usuario no existe.");
                    iniUsuario.requestFocus();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}