package com.example.epubook.vista;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.controlador.ControlUsuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.Objects;

public class InicioSesion extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText iniUsuario, iniContr;
    private Button botonIni;
    private TextView iniRegistro;
    private TextView olv_Contrasenia;

    private ControlDialogos controlDialogos = new ControlDialogos(InicioSesion.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion);

        auth = FirebaseAuth.getInstance();

        iniUsuario = findViewById(R.id.ini_user);
        iniContr = findViewById(R.id.ini_ctrsenia);
        botonIni = findViewById(R.id.btini_iniciar);
        iniRegistro = findViewById(R.id.btini_reg);
        olv_Contrasenia = findViewById(R.id.ini_olvCtr);


        //Al pulsar botón de iniciar sesión.
        botonIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!comprobarUsuario() | !comprobarContr()){

                }else{
                    validarUsuario();

                }
            }
        });

        //Al pulsar text registrarse.
        iniRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioSesion.this, Registrarse.class);
                startActivity(intent);
                finish();
            }
        });

        //Al pulsar text contrasenia olvidada.
        olv_Contrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               controlDialogos.contraseniaOlvidada(InicioSesion.this, auth);
            }
        });

    }

    //Compruebo si el campo de usuario está vacío.
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

    //Compruebo si el campo de contrasenia está vacío.
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

    //Método para validar el usuario al iniciar sesión.
    public void validarUsuario(){
        String vUsuario = iniUsuario.getText().toString().trim();
        String vContrasenia = iniContr.getText().toString().trim();

        //Obtengo la referencia de la bd para obtener el usuario y poder comprobar que el usuario existe.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query comprobarUserBD = reference.orderByChild("user").equalTo(vUsuario);

        comprobarUserBD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //Si el usuario existe, prosigue el inicio de sesión.
                if(snapshot.exists()){
                    iniUsuario.setError(null);
                    iniContr.setError(null);

                    //Mediante la referencia de ComprobarUserBD, accedo a obtener su email.
                    for (DataSnapshot userSnapshot : snapshot.getChildren()){
                        String emailUsuario = userSnapshot.child("email").getValue(String.class);

                        //Iniciando sesión con el email que he obtenido con su usuario y la contraseña introducida por teclado.
                        auth.signInWithEmailAndPassword(emailUsuario, vContrasenia).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                //Obtengo los datos del usuario.
                                String nombreUsuario = userSnapshot.child("nombre").getValue(String.class);
                                String userUsuario = userSnapshot.child("user").getValue(String.class);

                                iniUsuario.setError(null);
                                iniContr.setError(null);

                                Toast.makeText(InicioSesion.this, "Has iniciado sesión", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InicioSesion.this, PantallaInicio.class);

                                //Se lo pasaré a la pantalla de inicio para la información de perfil.
                                intent.putExtra("nombre", nombreUsuario);
                                intent.putExtra("email", emailUsuario);
                                intent.putExtra("user", userUsuario);
                                intent.putExtra("ctrsenia", vContrasenia);

                                startActivity(intent);
                                finish();
                            }

                        //En caso de fallo.
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                iniContr.setError("La contraseña es incorrecta.");
                                iniContr.requestFocus();
                            }
                        });
                    }
                //Si el usuario no existe, mando mensaje de error.
                }else{
                    iniUsuario.setError("Usuario no existe.");
                    iniUsuario.requestFocus();

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

}