package com.example.epubook.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private ControlDialogos controlDialogos = new ControlDialogos(CambiarUser.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.editar_usuario);

        View vista = findViewById(R.id.vistaCambioUser);

        cambiarUsuario = findViewById(R.id.bteditUser);
        cancelar = findViewById(R.id.btcancel_User);

        usuario = findViewById(R.id.editUser);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(uid);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mostrarUsuario();

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDialogos.dialogoUser(vista, CambiarUser.this, reference, user);
            }
        });

        //Botón cambiar de usuario.
        cambiarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usuario.getText().toString().isEmpty()){
                    Toast.makeText(CambiarUser.this, "Debe especificar un usuario.", Toast.LENGTH_SHORT).show();
                }else{
                    //No hay cambios.
                    if(usuarioUser.equals(usuario.getText().toString())){
                          finish();
                    }else{
                        //Compruebo que no haya otro usuario con el mismo user introducido.
                        databaseReference.orderByChild("user").equalTo(usuario.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    usuario.setError("El usuario ya existe.");
                                    usuario.requestFocus();
                                }else{
                                    //Nodo user, cambio el valor por el nuevo user.
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

    //Muestro nombre de usuario en edittext.
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
