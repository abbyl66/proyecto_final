package com.example.epubook.controlador;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.epubook.R;
import com.example.epubook.fragments.ColeccionesFragment;
import com.example.epubook.fragments.LibrosFragment;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.vista.ColeccAdapter;
import com.example.epubook.vista.PantallaInicio;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ControlColecciones {

    Context context;

    public ControlColecciones(Context context){
        this.context = context;
    }

    public void nuevaColeccion(String nombreColeccion, Activity activity, Dialog dialog, BottomNavigationView bottomNavigationView) {

        //Creo nodo epubs donde guardaré los ficheros de cada ususario en firestorage.
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis colecciones.
        StorageReference referenceColecc = referenceUsuario.child("misColecciones/");
        StorageReference referenceMiColecc = referenceColecc.child(nombreColeccion+"/");
        //Era necesario crear un fichero vacío para crear una carpeta en firebase storage.
        StorageReference referenceVacio = referenceMiColecc.child("ficheroVacio");

        InputStream vacio = new ByteArrayInputStream(new byte[0]);
        UploadTask uploadTask = referenceVacio.putStream(vacio);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(activity, "Colección creada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                bottomNavigationView.setSelectedItemId(R.id.mab_coleccion);
            }
        });

    }

    public void mostrarColecciones(List<Coleccion> listaColecciones, ColeccAdapter coleccAdapter, TextView noColecc){

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis colecciones.
        StorageReference referenceColecc = referenceUsuario.child("misColecciones/");

        referenceColecc.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                noColecc.setVisibility(View.VISIBLE);
                listaColecciones.clear();
                for(StorageReference coleccionNombre : listResult.getPrefixes()){
                    noColecc.setVisibility(View.GONE);
                    Coleccion coleccion = new Coleccion(coleccionNombre.getName());
                    listaColecciones.add(coleccion);
                    coleccAdapter.notifyDataSetChanged();
                }
            }
        });

    }

}

