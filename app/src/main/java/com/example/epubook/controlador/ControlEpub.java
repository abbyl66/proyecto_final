package com.example.epubook.controlador;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.modelo.ArchivoEpub;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ControlEpub {

    Context context;

    public ControlEpub(Context context){
        this.context = context;
    }

    //Método que recoge los archivos epub.
    public void mostrarEpub(File direc, List<ArchivoEpub> aEpub, TextView noEpub){

        File[] archivos = direc.listFiles();
        if(archivos != null){
            for (File file : archivos){
                //Si es un directorio vuelve a abrirlo.
                if(file.isDirectory()){
                    mostrarEpub(file, aEpub, noEpub);
                }else{
                    //Especifico que sea de extensión .epub.
                    String nombreArch = file.getName().toLowerCase();
                    if(nombreArch.endsWith(".epub")){
                        noEpub.setVisibility(View.GONE);
                        //Obtengo datos del fichero.
                        String nombre = file.getName();
                        double tamanio = file.length();
                        long fechalong = file.lastModified();
                        String uri = file.getAbsolutePath();

                        Date fecha = new Date(fechalong);

                        //Creo objeto y lo añado a mi lista de archivos.
                        ArchivoEpub nuevoArchivo = new ArchivoEpub(nombre, uri, tamanio, fecha);
                        aEpub.add(nuevoArchivo);
                    }
                }
            }
        }else{
            noEpub.setVisibility(View.VISIBLE);
        }

    }

    //Método para recibir el fichero que el usuario desea agregar a la app.
    public void aniadirArchivoEpub(String ruta){
        //Creo fichero mediante la ruta pasada por parámetro
        File epub = new File(ruta);
        Uri uri = Uri.fromFile(epub);

        //Creo nodo epubs donde guardaré los ficheros de cada ususario en firestorage.
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference referenceUsuario = reference.child("epubs").child(uidUsuario).child(uidUsuario+"_archivosEpub");

        //Control de fichero existente.
        if(epub.exists()){
            referenceUsuario.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(context, "SI", Toast.LENGTH_SHORT).show();
            });
        }else{
            Toast.makeText(context, "NO", Toast.LENGTH_SHORT).show();
        }

    }


}
