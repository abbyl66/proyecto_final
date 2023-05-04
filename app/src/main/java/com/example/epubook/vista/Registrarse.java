package com.example.epubook.vista;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registrarse extends AppCompatActivity {

    private EditText regNombre, regEmail, regUser, regContr;
    private TextView regIniciar;
    private Button botonReg;

    //Variables para Firebase Realtime Database.
    private FirebaseDatabase database;
    private DatabaseReference reference;

    //Variables para Firebase Auth.
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        //Asigno y relaciono cada variable con su campo del layout.
        auth = FirebaseAuth.getInstance();
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

                //Controlo que los campos no estén vacíos.
                if(nombre.isEmpty() || email.isEmpty() || usuario.isEmpty() || contrasenia.isEmpty() ){
                    Toast.makeText(Registrarse.this, "Debe rellenar todos los campos.", Toast.LENGTH_SHORT).show();
                }else{
                    //Firebase auth: guardo el usuario con su email y contrasenia.
                    auth.createUserWithEmailAndPassword(email, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //Asigno a nuevo usuario un id: user.getUid().
                                FirebaseUser user = auth.getCurrentUser();

                                //Realtime Database: guardo el usuario con sus datos en la bd realtime.
                                Usuario nuevoUsuario = new Usuario(nombre, email, usuario, contrasenia);
                                reference.child(user.getUid()).setValue(nuevoUsuario);

                                //Mando mensaje de que se ha registrado y redirecciono al inicio de sesión.
                                Toast.makeText(Registrarse.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Registrarse.this, InicioSesion.class);
                                startActivity(intent);
                                finish();

                            }else{
                                //En caso de que el registro falle.
                                Toast.makeText(Registrarse.this, "El registro ha fallado." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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