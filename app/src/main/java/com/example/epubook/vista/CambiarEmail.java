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
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.controlador.ControlEmail;
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

    ControlEmail controlEmail = new ControlEmail(CambiarEmail.this);
    ControlDialogos controlDialogos = new ControlDialogos(CambiarEmail.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.editar_email);

        View vista = findViewById(R.id.vistaEditarEmail);

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
                //Compruebo que el usuario haya accedido.
                if(user != null){
                    //Recargo los datos del usuario.
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            //Al pulsar al botón verificado, este comprueba si se verificó para terminar el proceso de cambio de email.
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

        //Si le da a cancelar, se mostrará un diálogo para confirmar que se quiere cancelar el cambio de email.
        cancelar.setOnClickListener(new View.OnClickListener() { //Dialogo
            @Override
            public void onClick(View view) {
                controlDialogos.dialogoCancelarEmail(vista, CambiarEmail.this, reference, user);
            }
        });

        //Botón para cambiar de email.
        cambiarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(CambiarEmail.this, "Debe especificar un email.", Toast.LENGTH_SHORT).show();
                }else{
                    //No ha hecho cambios.
                    if(emailUser.equals(email.getText().toString())){
                        finish();
                    }else{
                        //Compruebo que el email introducido no lo tenga en uso otro usuario.
                        databaseReference.orderByChild("email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    email.setError("El email ya existe.");
                                    email.requestFocus();

                                //De no ser así, procede al cambio de email.
                                }else{
                                    controlEmail.nuevoEmail(user, email, contrasenia, emailUser, cambiarEmail, verificado, infoVerificado, reference);
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

    //Método que muestra el email del usuario en el Edittext.
    public void mostrarEmail(){
        //A partir de la  referencia, obtengo el usuario con su método getEmail para obtener su email.
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

    //Si el usuario le da al botón atrás de su dispositivo, los cambios no se guardarán.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        controlEmail.noCambios(reference, user, CambiarEmail.this);
    }

}
