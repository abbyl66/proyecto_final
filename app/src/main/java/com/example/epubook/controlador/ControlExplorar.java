package com.example.epubook.controlador;

import android.content.Context;

import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.CategoriaAdapter;
import com.example.epubook.vista.ExpCabeceraAdapter;
import com.example.epubook.vista.PantallaExplorar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;

public class ControlExplorar {
    Context context;

    public ControlExplorar(Context context){
        this.context = context;
    }

    public void mostrarLibrosExp(List<Libro> listaLibros, ExpCabeceraAdapter cabeceraAdapter, PantallaExplorar pantallaExplorar) {
        ControlEpub controlEpub = new ControlEpub(pantallaExplorar);

        listaLibros.clear();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        //Nodo explorar
        StorageReference referenceExpl = reference.child("AAAExplorar");

        referenceExpl.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference epub : listResult.getItems()){

                    try {
                        StorageReference referenceArchivo = referenceExpl.child(epub.getName());

                        File archivoTemp = File.createTempFile(epub.getName(), "epub");
                        referenceArchivo.getFile(archivoTemp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                List<Libro> libros = controlEpub.mostrarMisLibros(archivoTemp.getAbsolutePath());
                                listaLibros.addAll(libros);
                                cabeceraAdapter.notifyDataSetChanged();
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });


    }

    public void mostrarCategorias(List<String> listaCategorias, CategoriaAdapter categoriaAdapter) {

        listaCategorias.clear();
        //Lista que usaré para que no se repitan las categorías.
        List<String> categorias = new ArrayList<>();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        //Nodo explorar
        StorageReference referenceExpl = reference.child("AAAExplorar");

        referenceExpl.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference epub : listResult.getItems()){

                    StorageReference referenceArchivo = referenceExpl.child(epub.getName());

                    try {

                        File archivoTemp = File.createTempFile(epub.getName(), "epub");
                        referenceArchivo.getFile(archivoTemp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                try {
                                    //Obtengo la categoria.
                                    EpubReader reader = new EpubReader();
                                    Book libroEpub = reader.readEpub(new FileInputStream(archivoTemp));
                                    Metadata metadata = libroEpub.getMetadata();

                                    String categoria = metadata.getSubjects().get(0);
                                    //Novela,Romance - Al dividirlo por una , coge la ultima parte despues de esta.
                                    String[] nombreEsp = categoria.split("\\,");
                                    categoria = nombreEsp[nombreEsp.length-1];//Romance

                                    if(!categorias.contains(categoria)){
                                        listaCategorias.add(categoria);
                                        categorias.add(categoria);
                                        categoriaAdapter.notifyDataSetChanged();
                                    }

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

    }
}
