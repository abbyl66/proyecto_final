package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.epubook.R;
import com.example.epubook.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatosPersonales extends AppCompatActivity {

    Button editarEmail, editarUsuario;
    TextView correo, usertxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.datos_personales);

        correo = findViewById(R.id.correoUser);
        usertxt = findViewById(R.id.usuarioUser);

        editarEmail = findViewById(R.id.editarEmail);
        editarUsuario = findViewById(R.id.editarUsuario);

        infoUsuario();

        editarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DatosPersonales.this, CambiarEmail.class);
                startActivity(intent);
            }
        });

        editarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DatosPersonales.this, CambiarUser.class);
                startActivity(intent);
            }
        });

    }

    private void infoUsuario() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){

            String uid = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users");
            DatabaseReference databaseReference = reference.child(uid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    String user = usuario.getUser();
                    String email = usuario.getEmail();

                    usertxt.setText(user);
                    correo.setText(email);

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }
    }
}