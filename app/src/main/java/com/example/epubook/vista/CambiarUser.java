package com.example.epubook.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.epubook.R;
import com.example.epubook.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CambiarUser extends AppCompatActivity {

    private Button cambiarUsuario, cancelar;
    private EditText usuario;
    private String usuarioUser;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String uid;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_usuario);

        cambiarUsuario = findViewById(R.id.bteditUser);
        cancelar = findViewById(R.id.btedit_cancelar);

        usuario = findViewById(R.id.editUser);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(uid);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mostrarUsuario();

        cancelar.setOnClickListener(new View.OnClickListener() { //Dialogo
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cambiarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usuario.getText().toString().isEmpty()){
                    Toast.makeText(CambiarUser.this, "Debe especificar un usuario.", Toast.LENGTH_SHORT).show();
                }else{
                    if(usuarioUser.equals(usuario.getText().toString())){
                        reference.child("user").setValue(usuario.getText().toString());
                        Toast.makeText(CambiarUser.this, "Usuario cambiado.", Toast.LENGTH_SHORT).show();//Cambiar por dialogs, seguro que quieres cambiar..?
                        finish();
                    }else{
                        databaseReference.orderByChild("user").equalTo(usuario.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    usuario.setError("El usuario ya existe.");
                                    usuario.requestFocus();
                                }else{
                                    reference.child("user").setValue(usuario.getText().toString());
                                    Toast.makeText(CambiarUser.this, "Usuario cambiado.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }
                }
            }
        });

    }

    public void mostrarUsuario(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);

                usuarioUser = user.getUser();

                usuario.setText(usuarioUser);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

}
