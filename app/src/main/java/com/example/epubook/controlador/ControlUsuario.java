package com.example.epubook.controlador;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class ControlUsuario {

    Context context;

    public ControlUsuario(Context context){
        this.context = context;
    }

    //Método para abrir activity.
    public void abrirActivity(Activity activity, Class activity2){
        Intent intent = new Intent(activity, activity2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }



}
