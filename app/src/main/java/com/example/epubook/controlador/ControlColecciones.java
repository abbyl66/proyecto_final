package com.example.epubook.controlador;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.ColeccAdapter;
import com.example.epubook.vista.ColeccDialogAdapter;
import com.example.epubook.vista.LibroColeccAdapter;
import com.example.epubook.vista.PantallaInicio;
import com.example.epubook.vista.PantallaPerfil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

                //Notifico a historial.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

                reference.child("historial").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            List<String> listaAcc = (List<String>) snapshot.getValue();

                            listaAcc.add("Has creado "+ nombreColeccion+ " a tus colecciones.");
                            reference.child("historial").setValue(listaAcc);
                            Toast.makeText(activity, "Colección creada", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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

    public void aniadirLibro(List<Coleccion> colecciones, String ruta, ImageView guardarColecc, View itemView) {
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

                        //Notifico a historial.
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

                        reference.child("historial").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){

                                    String arch = archivo.getName();
                                    int index = arch.lastIndexOf('.');
                                    String nombreArch = arch.substring(0, index);

                                    List<String> listaAcc = (List<String>) snapshot.getValue();

                                    listaAcc.add("Has añadido "+ nombreArch+ " a "+coleccion.getNombre());
                                    reference.child("historial").setValue(listaAcc);
                                    Toast.makeText(itemView.getContext(), "Libro guardado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        guardarColecc.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.celeste));
                    }
                });

            }else{
                System.out.println("No hay una coleccion seleccionada");
            }
        }
    }

    public void mostrarContenidoColecc(List<Libro> listaLibros, LibroColeccAdapter libroAdapter, Coleccion pos, FragmentActivity activity, TextView noLibrosColecc, RecyclerView recyclerCotenido) {

        controlEpub = new ControlEpub(activity);
        listaLibros.clear();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis colecciones y mis libros.
        StorageReference referenceColecc = referenceUsuario.child("misColecciones/");
        StorageReference referenceLibro = referenceUsuario.child("misLibros/");

        //Obtengo el nombre de la colección que quiero visualizar.
        StorageReference referenceMiColecc = referenceColecc.child(pos.getNombre());

        //Listo los libros que hay en mi colección personalizada.
        referenceMiColecc.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {

            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference storageReference : listResult.getItems()){
                    referenceLibro.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for(StorageReference epub : listResult.getItems()){

                                //Obtengo ese libro igualando el nombre con los tiulo de mis libros.
                                if(storageReference.getName().equals(epub.getName())){
                                    StorageReference referenceArchivo = referenceLibro.child(epub.getName());
                                    try {
                                        //Creo un fichero temporal al libro encontrado.
                                        File archivoTemp = File.createTempFile(epub.getName(), "epub");

                                        //Obtengo el fichero.
                                        referenceArchivo.getFile(archivoTemp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                //Lo guardo en la lista de libros que mostraré.
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

    public void eliminarColeccion(int position, List<Coleccion> listaColecciones, ColeccAdapter adapter, Coleccion coleccion, View view) {

        //Accedo a firebase storage.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Referencia a la carpeta que se eliminará.
        StorageReference referenceColecc = storage.getReference().child(uid).child("misColecciones").child(coleccion.getNombre());

        //Primero es necesario vaciar la carpeta.
        vaciarColeccion(referenceColecc, adapter);

        //Al vaciar la carpeta, esta se eliminará al no tener nada.
        listaColecciones.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();

        PantallaInicio pantallaInicio = new PantallaInicio();
        pantallaInicio.bottomNavigationView.setSelectedItemId(R.id.mab_libros);

        //Notifico a historial.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

        reference.child("historial").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    List<String> listaAcc = (List<String>) snapshot.getValue();

                    listaAcc.add("Has eliminado la colección "+ coleccion.getNombre()+ ".");
                    reference.child("historial").setValue(listaAcc);
                    Toast.makeText(view.getContext(), "Colección eliminada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void vaciarColeccion(StorageReference referenceLibro, ColeccAdapter adapter) {
        referenceLibro.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference coleccion : listResult.getItems()){
                    coleccion.delete();

                }
            }
        });

    }

}

