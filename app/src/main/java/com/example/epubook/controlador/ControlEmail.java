package com.example.epubook.controlador;

import android.app.Activity;
import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.modelo.Usuario;
import com.example.epubook.vista.CambiarEmail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;

public class ControlEmail {

    Context context;

    public ControlEmail(Context context){
        this.context = context;
    }

    //En caso de que se cancele el proceso de cambio de email, se reestablece el email anterior.
    public void noCambios(DatabaseReference reference, FirebaseUser user, Activity activity){
        //A partir de la referencia obtengo al usuario.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                //Actualizo nuevamente el email con el que tiene el usuario en la bd, que sería el email anterior.
                user.updateEmail(usuario.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        //Finalizo activity, vuelve a la pantalla de perfil.
                        activity.finish();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    //Método para cambiar el email.
    public void nuevoEmail(FirebaseUser user, EditText email, EditText contrasenia, String emailUser, Button cambiarEmail, Button verificado, TextView infoVerificado, DatabaseReference reference){
        //Compruebo que haya accedido un usuario.
        if (user != null){
            //Refresca información del usuario.
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    //Compruebo que haya verificado el email que tiene actualmente.
                    if(user.isEmailVerified()){
                        //Comrpuebo que el email coincida con el formato correcto.
                        if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                            String nuevoEmail = email.getText().toString();

                            //Es necesario que haga una autenticación del usuario para poder cambiar su email.
                            AuthCredential credential = EmailAuthProvider.getCredential(emailUser, contrasenia.getText().toString());

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    //Si la autenticación es correcta, uso el método updateEmail para actualizar el email.
                                    if(task.isSuccessful()){
                                        user.updateEmail(nuevoEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task) {
                                                //Cuando cambie el email, uso el método sendEmailVerification para mandar un correo de verificación.
                                                if(task.isSuccessful()){
                                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(Task<Void> task) {
                                                            //Al mandar el correo de verificación, habilito el botón verificado para que una vez se verifique el email, queden guardado el cambio de email.
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(context, "Verifica tu correo", Toast.LENGTH_SHORT).show();
                                                                verificado.setVisibility(View.VISIBLE);
                                                                infoVerificado.setVisibility(View.VISIBLE);
                                                                email.setEnabled(false);
                                                                contrasenia.setEnabled(false);
                                                                cambiarEmail.setEnabled(false);


                                                            }
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{
                            email.setError("Formato incorrecto");
                            email.requestFocus();
                        }
                    //Si no se ha verificado el correo.
                    }else{
                        //En caso de que el correo de firebaseAuth y firebase realtime coincidan, quiere decir que sí se ha verificado. Por lo tanto pasa al método de cambio de email sin pasar por isVerified.
                        if(emailUser.equals(user.getEmail())){
                            nuevoEmailVer(user, email, contrasenia, emailUser, cambiarEmail, verificado, infoVerificado, reference);
                        }else{
                            Toast.makeText(context, "Verifica tu email antes de cambiarlo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    }

    //Método igual que email, sin pasar por el método isVerified.
    public void nuevoEmailVer(FirebaseUser user, EditText email, EditText contrasenia, String emailUser, Button cambiarEmail, Button verificado, TextView infoVerificado, DatabaseReference reference){
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
                                                            Toast.makeText(context, "Verifica tu correo", Toast.LENGTH_SHORT).show();
                                                            verificado.setVisibility(View.VISIBLE);
                                                            infoVerificado.setVisibility(View.VISIBLE);
                                                            email.setEnabled(false);
                                                            contrasenia.setEnabled(false);
                                                            cambiarEmail.setEnabled(false);


                                                        }
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
