package com.example.epubook.controlador;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.ColeccAdapter;
import com.example.epubook.vista.ColeccDialogAdapter;
import com.example.epubook.vista.LibroColeccAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ControlColecciones {

    Context context;
    ControlEpub controlEpub;

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

    //Colecciones del fragment colecciones.
    public void mostrarColecciones(List<Coleccion> listaColecciones, ColeccAdapter coleccAdapter){

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
                listaColecciones.clear();
                for(StorageReference coleccionNombre : listResult.getPrefixes()){
                    Coleccion coleccion = new Coleccion(coleccionNombre.getName(), false);
                    listaColecciones.add(coleccion);
                    coleccAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    //Colecciones que se muestran al momento de guardar un libro.
    public void mostrarColeccionesRecycler(List<Coleccion> listaColecciones, ColeccDialogAdapter coleccAdapter, TextView noColecc){

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
                    Coleccion coleccion = new Coleccion(coleccionNombre.getName(), false);
                    listaColecciones.add(coleccion);
                    coleccAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void aniadirLibro(List<Coleccion> colecciones, String ruta) {
        for(Coleccion coleccion : colecciones){
            if(coleccion.isSeleccionado()){
                File archivo = new File(ruta);

                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference reference = firebaseStorage.getReference();

                String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //Nodo usuario.
                StorageReference referenceUsuario = reference.child(uidUsuario);

                //Nodo mis colecciones.
                StorageReference referenceColecc = referenceUsuario.child("misColecciones/");
                StorageReference referenceColeccUser = referenceColecc.child(coleccion.getNombre());
                StorageReference referenceRuta = referenceColeccUser.child(archivo.getName());
                referenceRuta.putBytes(ruta.getBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Libro guardado");
                    }
                });

            }else{
                System.out.println("No hay una coleccion seleccionada");
            }
        }
    }

    public void mostrarContenidoColecc(List<Libro> listaLibros, LibroColeccAdapter libroAdapter, Coleccion pos, FragmentActivity activity) {

        controlEpub = new ControlEpub(activity);
        listaLibros.clear();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis colecciones.
        StorageReference referenceColecc = referenceUsuario.child("misColecciones/");
        StorageReference referenceLibro = referenceUsuario.child("misLibros/");
        StorageReference referenceMiColecc = referenceColecc.child(pos.getNombre());

        referenceMiColecc.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {

            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference storageReference : listResult.getItems()){
                    referenceLibro.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for(StorageReference epub : listResult.getItems()){

                                if(storageReference.getName().equals(epub.getName())){
                                    StorageReference referenceArchivo = referenceLibro.child(epub.getName());
                                    try {
                                        File archivoTemp = File.createTempFile(epub.getName(), "epub");
                                        referenceArchivo.getFile(archivoTemp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                List<Libro> libros = controlEpub.mostrarMisLibros(archivoTemp.getAbsolutePath());
                                                listaLibros.addAll(libros);
                                                libroAdapter.notifyDataSetChanged();


                                            }
                                        });
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }


                                }else{
                                    listaLibros.clear();
                                    libroAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }
        });


    }
}

