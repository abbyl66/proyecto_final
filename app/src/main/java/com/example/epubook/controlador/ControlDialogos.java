package com.example.epubook.controlador;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.epubook.R;
import com.example.epubook.fragments.ColeccionesFragment;
import com.example.epubook.modelo.ArchivoEpub;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.ArchivosEpub;
import com.example.epubook.vista.EpubAdapter;
import com.example.epubook.vista.LibroAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ControlDialogos {

    Context context;

    public ControlDialogos(Context context){
        this.context = context;
    }

    ControlEmail controlEmail = new ControlEmail(context);
    ControlColecciones controlColecciones = new ControlColecciones(context);
    ControlEpub controlEpub = new ControlEpub(context);

    //Métodos para mostrar dialogo de confirmación:

    // Pedido de permisos de ficheros.
    public void dialogoConfirmacion(View vista, Activity activity){
        //Variables del dialog personalizado.
        ConstraintLayout confirmacion = vista.findViewById(R.id.dialogoConfirm);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogo_confirmacion, confirmacion);

        TextView tituloDialog, infoDialog;
        Button cancelarDialog, aceptarDialog;

        tituloDialog = view.findViewById(R.id.confirmTitulo);
        infoDialog = view.findViewById(R.id.infoConfirm);
        cancelarDialog = view.findViewById(R.id.btcancelar);
        aceptarDialog = view.findViewById(R.id.btaceptar);

        //Creo alertdialog y le doy el diseño con el layout.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        //Muestro información en el dialog.
        tituloDialog.setText("Permisos");
        infoDialog.setText("Debe dar permisos de acceso para poder mostrar sus archivos.");
        cancelarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón cancelar.
                alertDialog.dismiss();
                Toast.makeText(context, "No se pueden mostrar sus archivos.", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });

        aceptarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón aceptar.
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivity(intent);
                alertDialog.dismiss();
            }
        });

        //Muestra diálogo.
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    //Salir de cambiar email.
    public void dialogoCancelarEmail(View vista, Activity activity, DatabaseReference reference, FirebaseUser user){
        //Variables del dialog personalizado.
        ConstraintLayout confirmacion = vista.findViewById(R.id.dialogoConfirm);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogo_confirmacion, confirmacion);

        TextView tituloDialog, infoDialog;
        Button cancelarDialog, aceptarDialog;

        tituloDialog = view.findViewById(R.id.confirmTitulo);
        infoDialog = view.findViewById(R.id.infoConfirm);
        cancelarDialog = view.findViewById(R.id.btcancelar);
        aceptarDialog = view.findViewById(R.id.btaceptar);

        //Creo alertdialog y le doy el diseño con el layout.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        //Muestro información en el dialog.
        tituloDialog.setText("¿Desea salir?");
        infoDialog.setText("Se perderá el proceso de cambio de email.");
        cancelarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón cancelar.
                alertDialog.dismiss();
            }
        });

        aceptarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón aceptar.
                alertDialog.dismiss();
                controlEmail.noCambios(reference, user, activity);
            }
        });

        //Muestra diálogo.
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    //Salir de cambio de nombre.
    public void dialogoNombre(View vista, Activity activity, DatabaseReference reference, FirebaseUser user){
        //Variables del dialog personalizado.
        ConstraintLayout confirmacion = vista.findViewById(R.id.dialogoConfirm);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogo_confirmacion, confirmacion);

        TextView tituloDialog, infoDialog;
        Button cancelarDialog, aceptarDialog;

        tituloDialog = view.findViewById(R.id.confirmTitulo);
        infoDialog = view.findViewById(R.id.infoConfirm);
        cancelarDialog = view.findViewById(R.id.btcancelar);
        aceptarDialog = view.findViewById(R.id.btaceptar);

        //Creo alertdialog y le doy el diseño con el layout.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        //Muestro información en el dialog.
        tituloDialog.setText("¿Desea salir?");
        infoDialog.setText("Se perderá el proceso de cambio de nombre.");
        cancelarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón cancelar.
                alertDialog.dismiss();
            }
        });

        aceptarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón aceptar.
                alertDialog.dismiss();
                activity.finish();
            }
        });

        //Muestra diálogo.
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    //Salir de cambio de user.
    public void dialogoUser(View vista, Activity activity, DatabaseReference reference, FirebaseUser user){
        //Variables del dialog personalizado.
        ConstraintLayout confirmacion = vista.findViewById(R.id.dialogoConfirm);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogo_confirmacion, confirmacion);

        TextView tituloDialog, infoDialog;
        Button cancelarDialog, aceptarDialog;

        tituloDialog = view.findViewById(R.id.confirmTitulo);
        infoDialog = view.findViewById(R.id.infoConfirm);
        cancelarDialog = view.findViewById(R.id.btcancelar);
        aceptarDialog = view.findViewById(R.id.btaceptar);

        //Creo alertdialog y le doy el diseño con el layout.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        //Muestro información en el dialog.
        tituloDialog.setText("¿Desea salir?");
        infoDialog.setText("Se perderá el proceso de cambio de nombre de usuario.");
        cancelarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón cancelar.
                alertDialog.dismiss();
            }
        });

        aceptarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón aceptar.
                alertDialog.dismiss();
                activity.finish();
            }
        });

        //Muestra diálogo.
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


    //Método para cambiar de contrasenia a través de un dialog.
    public void contraseniaOlvidada(Activity activity, FirebaseAuth auth){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View d_contrasenia = activity.getLayoutInflater().inflate(R.layout.dialogo_contrasenia, null);
        EditText email = d_contrasenia.findViewById(R.id.olv_email);

        builder.setView(d_contrasenia);
        AlertDialog alertDialog = builder.create();

        //Al pulsar en el botón cambiar del dialog.
        d_contrasenia.findViewById(R.id.btolv_cambiar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuarioEmail = email.getText().toString();

                //Compruebo correo introducido.
                if(TextUtils.isEmpty(usuarioEmail) && !Patterns.EMAIL_ADDRESS.matcher(usuarioEmail).matches()){
                    email.setError("Ingresa tu email.");
                    email.requestFocus();
                    return;
                }

                //Firebase auth: método para reestablecer contrasenia.
                auth.sendPasswordResetEmail(usuarioEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Comprueba tu bandeja de correos.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }else {
                            email.setError("Email incorrecto.");
                            email.requestFocus();
                        }
                    }
                });

            }
        });

        //Al pulsar botón cancelar del dialog.
        d_contrasenia.findViewById(R.id.btolv_cancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cierra dialog.
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    //Al pulsar sobre añadir nuevo en el menú inferior. Muestra su diálogo.
    public void mostrarDialogoAniadir(Activity activity, BottomNavigationView bottomNavigationView){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_aniadir);

        LinearLayout nuevoLibro = dialog.findViewById(R.id.an_libro);
        LinearLayout nuevaColecc = dialog.findViewById(R.id.an_coleccion);

        //Opciones que se pueden realizar desde el diálogo.
        nuevoLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, ArchivosEpub.class);
                activity.startActivity(intent);

            }
        });

        nuevaColecc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Variables del dialog personalizado.
                ConstraintLayout confirmacion = view.findViewById(R.id.layout_dialogoColecc);
                View vista = LayoutInflater.from(context).inflate(R.layout.activity_add_colecciones, confirmacion);

                EditText nombreColecc;
                Button cancelarDialog, aceptarDialog;

                nombreColecc = vista.findViewById(R.id.txtColecc);
                cancelarDialog = vista.findViewById(R.id.cancelarColecc);
                aceptarDialog = vista.findViewById(R.id.coleccCrear);

                //Creo alertdialog y le doy el diseño con el layout.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(vista);
                final AlertDialog alertDialog = builder.create();

                cancelarDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Si le da al botón cancelar.
                        alertDialog.dismiss();
                    }
                });

                aceptarDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Si le da al botón aceptar.
                        alertDialog.dismiss();
                        String nombreColeccion = nombreColecc.getText().toString();
                        if(nombreColeccion.isEmpty()){
                            nombreColecc.setError("Debe introducir un nombre.");
                            nombreColecc.setFocusable(true);
                        }else{
                            controlColecciones.nuevaColeccion(nombreColeccion, activity, dialog, bottomNavigationView);
                        }

                    }
                });

                //Muestra diálogo.
                if(alertDialog.getWindow() != null){
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                alertDialog.show();

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.animacionDialogo;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    //Dialogo eliminar item
    public void dialogoEliminarItem(View vista, int pos, List<Libro> listalibros, LibroAdapter libroAdapter){
        //Variables del dialog personalizado.
        ConstraintLayout confirmacion = vista.findViewById(R.id.dialogoConfirm);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogo_confirmacion, confirmacion);

        TextView tituloDialog, infoDialog;
        Button cancelarDialog, aceptarDialog;

        tituloDialog = view.findViewById(R.id.confirmTitulo);
        infoDialog = view.findViewById(R.id.infoConfirm);
        cancelarDialog = view.findViewById(R.id.btcancelar);
        aceptarDialog = view.findViewById(R.id.btaceptar);

        //Creo alertdialog y le doy el diseño con el layout.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        Libro epub = listalibros.get(pos);

        //Muestro información en el dialog.
        tituloDialog.setText("¿Desea eliminarlo?");
        infoDialog.setText("Se eliminará: '" +epub.getTitulo()+"'.");
        cancelarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón cancelar.
                libroAdapter.notifyItemChanged(pos);
                alertDialog.dismiss();
            }
        });

        aceptarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si le da al botón aceptar.
                alertDialog.dismiss();
                controlEpub.eliminarEpub(pos, listalibros, libroAdapter);
            }
        });

        //Muestra diálogo.
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        //En caso de darle al botón atrás desde el dispositvo, no lo permitirá.
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    return true;
                }
                return false;
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}