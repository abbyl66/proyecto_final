package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class CambiarContrasenia extends AppCompatActivity {

    private TextInputEditText contrActual, contrNuev1, contrNuev2;
    private Button cambiarContr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.activity_cambiar_contrasenia);

        contrActual = findViewById(R.id.contrActual);
        contrNuev1 = findViewById(R.id.contrNueva1);
        contrNuev2 = findViewById(R.id.contrNueva2);
        cambiarContr = findViewById(R.id.btCambiarContr);

        cambiarContr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                            //Obtengo el email y usuario del usuario actual para cambiar la contreseña.
                            String contr = usuario.getCtrsenia();
                            String email = usuario.getEmail();
                            //Control de errores.
                            if(contrActual.getText().toString().isEmpty()){
                                contrActual.setError("Campo vacío");
                                contrActual.requestFocus();
                            }else if(contrActual.getText().toString().equals(contr)){

                                //Es necesario autenticar al usuario para poder actualizar su contrasenia.
                                auth.signInWithEmailAndPassword(email, contr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            //Controlo errores.
                                            if(contrNuev1.getText().toString().isEmpty()){
                                                contrNuev1.setError("Campo vacío");
                                                contrNuev1.requestFocus();
                                            }else if(contrNuev2.getText().toString().isEmpty()) {
                                                contrNuev2.setError("Campo vacío");
                                                contrNuev2.requestFocus();
                                            //Si las dos contraseñas nuevas coinciden, procede a cambiarse.
                                            }else if(contrNuev1.getText().toString().equals(contrNuev2.getText().toString())){
                                                user.updatePassword(contrNuev1.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            databaseReference.child("ctrsenia").setValue(contrNuev1.getText().toString());
                                                            Toast.makeText(CambiarContrasenia.this, "Contraseña cambiada.", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }else{
                                                contrNuev1.setError("Contraseña no coincide");
                                                contrNuev2.setError("Contraseña no coincide");
                                            }
                                        }
                                    }
                                });

                            }else{
                                contrActual.setError("Contraseña inválida");
                                contrActual.requestFocus();
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