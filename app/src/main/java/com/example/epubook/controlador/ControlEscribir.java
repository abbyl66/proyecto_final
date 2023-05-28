package com.example.epubook.controlador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.epubook.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.integrity.i;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

public class ControlEscribir {
    Context context;

    public ControlEscribir(Context context){
        this.context = context;
    }

    public void crearLibro(String titulo, String autor, String imagenSeleccionada) {

        Book libro = new Book();
        libro.getMetadata().addTitle(titulo);

        libro.getMetadata().addAuthor(new Author(autor));

        libro.getMetadata().addType("libre");

        Resource portada = libro.getResources().add(new Resource(imagenSeleccionada));
        libro.setCoverImage(portada);

        Resource resource = new Resource("<html><body><h1>Primer capitulo</h1><p>Contenido</p></body></html>");

        libro.addSection("Capitulo por defecto", resource);

        try{
            EpubWriter epubWriter = new EpubWriter();
            File archivoTemp = File.createTempFile(titulo, ".epub");
            epubWriter.write(libro, new FileOutputStream(archivoTemp));

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference reference = firebaseStorage.getReference();

            String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //Nodo usuario, donde guardaré los ficheros epub de cada usuario.
            StorageReference referenceUsuario = reference.child(uidUsuario);

            //Nodo mis libros y epub.
            StorageReference referenceLibros = referenceUsuario.child("escritosPorMi");
            StorageReference referenceEpub = referenceLibros.child(archivoTemp.getName());

            referenceEpub.putFile(Uri.fromFile(archivoTemp)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Libro creado", "Funciona");
                }
            });

        }catch (IOException e){
            e.printStackTrace();
        }

    }


}
