package com.example.epubook.vista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.epubook.R;
import com.example.epubook.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CambiarEmail extends AppCompatActivity {

    private Button cambiarEmail, cancelar, verificado;
    private EditText email, contrasenia;
    private TextView infoVerificado;
    private String emailUser;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String uid;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.editar_email);

        cambiarEmail = findViewById(R.id.bteditEmail);
        cancelar = findViewById(R.id.btedit_cancelar);

        email = findViewById(R.id.editEmail);
        contrasenia = findViewById(R.id.confirEmail);

        verificado = findViewById(R.id.btverificado);
        infoVerificado = findViewById(R.id.labelVerificado);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(uid);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        verificado.setVisibility(View.GONE);
        infoVerificado.setVisibility(View.GONE);

        mostrarEmail();

        //Botón que usuará el usuario para comprobar que ha verificado su email.
        verificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(user.isEmailVerified()){
                                reference.child("email").setValue(user.getEmail());
                                Toast.makeText(CambiarEmail.this, "Tu email se ha cambiado.", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(CambiarEmail.this, "Debes verificar tu email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Hacer dialogo para confirmar.
        cancelar.setOnClickListener(new View.OnClickListener() { //Dialogo
            @Override
            public void onClick(View view) {
                noCambios();
            }
        });

        cambiarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(CambiarEmail.this, "Debe especificar un email.", Toast.LENGTH_SHORT).show();
                }else{
                    if(emailUser.equals(email.getText().toString())){
                        finish();
                    }else{
                        databaseReference.orderByChild("email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    email.setError("El email ya existe.");
                                    email.requestFocus();
                                }else{
                                    nuevoEmail();
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

    public void mostrarEmail(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);

                emailUser = user.getEmail();

                email.setText(emailUser);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        noCambios();
    }

    public void noCambios(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                user.updateEmail(usuario.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        Toast.makeText(CambiarEmail.this, "No se han hecho cambios.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    public void nuevoEmail(){
        if (user != null){
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if(user.isEmailVerified()){
                        if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                            String nuevoEmail = email.getText().toString();

                            //Es necesario que haga una autenticación del usuario para poder cambiar su email.
                            AuthCredential credential = EmailAuthProvider.getCredential(emailUser, contrasenia.getText().toString());

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if(task.isSuccessful()){
                                        user.updateEmail(nuevoEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(CambiarEmail.this, "Verifica tu correo", Toast.LENGTH_SHORT).show();
                                                                verificado.setVisibility(View.VISIBLE);
                                                                infoVerificado.setVisibility(View.VISIBLE);
                                                                email.setEnabled(false);
                                                                contrasenia.setEnabled(false);
                                                                cambiarEmail.setEnabled(false);


                                                            }
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(CambiarEmail.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(CambiarEmail.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            email.setError("Formato incorrecto");
                            email.requestFocus();
                        }
                    }else{
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Usuario userEmail = snapshot.getValue(Usuario.class);
                                if(emailUser.equals(userEmail)){
                                    nuevoEmailVer();
                                }else{
                                    Toast.makeText(CambiarEmail.this, "Verifica tu email antes de cambiarlo.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }
                }
            });
        }

    }

    public void nuevoEmailVer(){
        if (user != null){
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                        String nuevoEmail = email.getText().toString();

                        //Es necesario que haga una autenticación del usuario para poder cambiar su email.
                        AuthCredential credential = EmailAuthProvider.getCredential(emailUser, contrasenia.getText().toString());

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updateEmail(nuevoEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if(task.isSuccessful()){
                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(CambiarEmail.this, "Verifica tu correo", Toast.LENGTH_SHORT).show();
                                                            verificado.setVisibility(View.VISIBLE);
                                                            infoVerificado.setVisibility(View.VISIBLE);
                                                            email.setEnabled(false);
                                                            contrasenia.setEnabled(false);
                                                            cambiarEmail.setEnabled(false);


                                                        }
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(CambiarEmail.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(CambiarEmail.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        email.setError("Formato incorrecto");
                        email.requestFocus();
                    }

                }
            });
        }
    }
}
