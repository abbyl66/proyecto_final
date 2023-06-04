package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Registrarse extends AppCompatActivity {

    private EditText regNombre, regEmail, regUser;
    private TextInputEditText regContr;
    private TextView regIniciar;
    private Button botonReg, verificar;

    //Variables para Firebase Realtime Database.
    private FirebaseDatabase database;
    private DatabaseReference reference;

    //Variables para Firebase Auth.
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.tema);
        setContentView(R.layout.activity_registrarse);

        //Asigno y relaciono cada variable con su campo del layout.
        auth = FirebaseAuth.getInstance();
        regNombre = findViewById(R.id.reg_nombre);
        regEmail = findViewById(R.id.reg_email);
        regUser = findViewById(R.id.reg_user);
        regContr = findViewById(R.id.reg_ctrsenia);
        botonReg = findViewById(R.id.btreg_regist);
        regIniciar = findViewById(R.id.btreg_iniciar);
        verificar = findViewById(R.id.verificarEmail);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        botonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarUsuario();
            }
        });

        verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(user.isEmailVerified()){
                                Toast.makeText(Registrarse.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Registrarse.this, PantallaInicio.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(Registrarse.this, "Debes verificar tu email", Toast.LENGTH_SHORT).show();
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

    public void comprobarUsuario(){

        reference.orderByChild("user").equalTo(regUser.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    regUser.setError("Usuario existente");
                    regUser.requestFocus();
                }else{
                    registarUsuario();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    public void registarUsuario(){

        StorageReference refFoto = FirebaseStorage.getInstance().getReference().child("AAAUsuarios/user.png");

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
                public void onComplete(Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Asigno a nuevo usuario un id: user.getUid().
                        FirebaseUser user = auth.getCurrentUser();

                        //Recogo foto de perfil por defecto que guardo en firebase storage.
                        refFoto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                List<String> historial = new ArrayList<>();
                                historial.add("Te has unido a Epubook.");
                                //Realtime Database: guardo el usuario con sus datos en la bd realtime.
                                Usuario nuevoUsuario = new Usuario(nombre, email, usuario, contrasenia, String.valueOf(uri), historial);
                                reference.child(user.getUid()).setValue(nuevoUsuario);

                            }
                        });

                        //Mando mensaje de que se ha registrado y enviado correo de verificación de email.
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                Toast.makeText(Registrarse.this, "No cierres la app. Verifica tu email", Toast.LENGTH_SHORT).show();
                                botonReg.setVisibility(View.GONE);
                                verificar.setVisibility(View.VISIBLE);
                                regNombre.setEnabled(false);
                                regEmail.setEnabled(false);
                                regUser.setEnabled(false);
                                regContr.setEnabled(false);
                            }
                        });

                    }else{

                        //Control de errores. Editando mensajes.
                        switch (task.getException().getMessage()){
                            case "The email address is badly formatted.":
                                regEmail.setError("Formato incorrecto.");
                                regEmail.requestFocus();
                                break;
                            case "The given password is invalid. [ Password should be at least 6 characters ]":
                                regContr.setError("Debe tener al menos 6 caracteres.");
                                regContr
                                        .requestFocus();
                                break;
                            case "The email address is already in use by another account.":
                                regEmail.setError("Email en uso.");
                                regEmail.requestFocus();
                                break;
                        }

                    }
                }
            });

        }
    }
}