package com.example.epubook.vista;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CambiarNombre extends AppCompatActivity {

    private EditText nombre;
    private Button cancelar, cambiarNombre;
    private String nombreUser;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String uid;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference databaseReference;

    private ControlDialogos controlDialogos = new ControlDialogos(CambiarNombre.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.editar_nombre);

        View vista = findViewById(R.id.vistaCambioNombre);

        nombre = findViewById(R.id.editNombre);

        cambiarNombre = findViewById(R.id.bteditNombre);

        cancelar = findViewById(R.id.btcancel_Nom);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(uid);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mostrarNombre();


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDialogos.dialogoNombre(vista, CambiarNombre.this, reference, user);
            }
        });


        //Método para cambiar nombre.
        cambiarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombre.getText().toString().isEmpty()){
                    Toast.makeText(CambiarNombre.this, "Debe especificar un nombre.", Toast.LENGTH_SHORT).show();
                }else{
                    //Obtengo al usuario desde una referencia, cambio el valor de su nodo "nombre" por el nuevo nombre introducido.
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            reference.child("nombre").setValue(nombre.getText().toString());
                            Toast.makeText(CambiarNombre.this, "Nombre cambiado.", Toast.LENGTH_SHORT).show();
                            CambiarNombre.this.finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
    }

    //Muestro nombre en el edittext.
    public void mostrarNombre(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);

                nombreUser = user.getNombre();

                nombre.setText(nombreUser);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }



}