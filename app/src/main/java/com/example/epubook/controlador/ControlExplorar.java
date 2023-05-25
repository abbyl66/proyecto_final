package com.example.epubook.controlador;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.modelo.LibroExplorar;
import com.example.epubook.vista.CategoriaAdapter;
import com.example.epubook.vista.ExpCabeceraAdapter;
import com.example.epubook.vista.LibroCatAdapter;
import com.example.epubook.vista.PantallaExplorar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    public static int numDescargas;

    public ControlExplorar(Context context){
        this.context = context;
    }

    //Método para mostrar libros de la bd, disponibles para descargar.
    public void mostrarLibrosExp(List<LibroExplorar> listaLibros, ExpCabeceraAdapter cabeceraAdapter, PantallaExplorar pantallaExplorar) {
        ControlEpub controlEpub = new ControlEpub(pantallaExplorar);

        listaLibros.clear();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        //Nodo explorar
        StorageReference referenceExpl = reference.child("AAAExplorar");

        referenceExpl.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                final int[] contador = {0};
                List<LibroExplorar> listaCont = new ArrayList<>();

                for(StorageReference epub : listResult.getItems()){

                    try {
                        StorageReference referenceArchivo = referenceExpl.child(epub.getName());
                        File archivoTemp = File.createTempFile(epub.getName(), "epub");

                        referenceArchivo.getFile(archivoTemp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                List<LibroExplorar> libros = controlEpub.mostrarLibrosExp(archivoTemp.getAbsolutePath());
                                listaCont.addAll(libros);
                                contador[0]++;

                                if(contador[0] == listResult.getItems().size()){
                                    listaLibros.addAll(listaCont);
                                    cabeceraAdapter.notifyDataSetChanged();
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

    //Método que muestra las categorías que hay según los libros guardados en la bd.
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

                                    //Muestro categorias sin repetir.
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

    public void mostrarLibrosCat(List<LibroExplorar> listaLibCat, LibroCatAdapter libroCatAdapter, String categoria, PantallaExplorar pantallaExplorar) {

        listaLibCat.clear();

        ControlEpub controlEpub = new ControlEpub(pantallaExplorar);

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

                                    String catLibro = metadata.getSubjects().get(0);

                                    String[] nombreEsp = catLibro.split("\\,");
                                    catLibro = nombreEsp[nombreEsp.length-1];

                                    //Añado libros que solo pertenezcan a la categoria seleccionada.
                                    if(categoria.equals(catLibro)){
                                        List<LibroExplorar> libros = controlEpub.mostrarLibrosExp(archivoTemp.getAbsolutePath());
                                        listaLibCat.addAll(libros);
                                        libroCatAdapter.notifyDataSetChanged();
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

    public void descargarLibroCab(String ruta, LibroExplorar libro, ExpCabeceraAdapter expCabeceraAdapter) {
        libro.setDescargando(true);
        //Creo fichero mediante la ruta pasada por parámetro
        File epub = new File(ruta);
        Uri uri = Uri.fromFile(epub);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario, donde guardaré los ficheros epub de cada usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis libros y epub.
        StorageReference referenceLibros = referenceUsuario.child("misLibros");
        StorageReference referenceEpub = referenceLibros.child(epub.getName());

        //Referencia mis libros, para comprobar que no haya un archivo igual.
        referenceLibros.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference libroRef : listResult.getItems()){

                    //Extraigo el nombre de la ruta.
                    String archivo = epub.getName();
                    int index = archivo.lastIndexOf('.');
                    String nombreArch = archivo.substring(0, index);

                    String archivo2 = libroRef.getName();
                    int index2 = archivo2.lastIndexOf('.');
                    String nombreArch2 = archivo2.substring(0, index2);

                    //Los igualo para saber si ya existe.
                    if(nombreArch.equals(nombreArch2)){
                        libro.setDescargando(false);
                        libro.setGuardado(true);
                        expCabeceraAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "El libro ya existe en tu biblioteca.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //Se obtendrá y guardará el fichero en el nodo mis libros.
                referenceEpub.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        libro.setDescargando(false);
                        libro.setGuardado(true);
                        expCabeceraAdapter.notifyDataSetChanged();
                        numDescargas = libro.getNumDescargas()+1;
                        libro.setNumDescargas(numDescargas);
                        Toast.makeText(context, "Libro descargado", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "No se ha podido descargar el libro.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    public void descargarLibroCat(String ruta, LibroExplorar libro, LibroCatAdapter libroCatAdapter) {
        libro.setDescargando(true);
        //Creo fichero mediante la ruta pasada por parámetro
        File epub = new File(ruta);
        Uri uri = Uri.fromFile(epub);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario, donde guardaré los ficheros epub de cada usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis libros y epub.
        StorageReference referenceLibros = referenceUsuario.child("misLibros");
        StorageReference referenceEpub = referenceLibros.child(epub.getName());

        //Referencia mis libros, para comprobar que no haya un archivo igual.
        referenceLibros.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference libroRef : listResult.getItems()){

                    //Extraigo el nombre de la ruta.
                    String archivo = epub.getName();
                    int index = archivo.lastIndexOf('.');
                    String nombreArch = archivo.substring(0, index);

                    String archivo2 = libroRef.getName();
                    int index2 = archivo2.lastIndexOf('.');
                    String nombreArch2 = archivo2.substring(0, index2);

                    //Los igualo para saber si ya existe.
                    if(nombreArch.equals(nombreArch2)){
                        libro.setDescargando(false);
                        libro.setGuardado(true);
                        libroCatAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "El libro ya existe en tu biblioteca.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //Se obtendrá y guardará el fichero en el nodo mis libros.
                referenceEpub.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        libro.setDescargando(false);
                        libro.setGuardado(true);
                        libroCatAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "Libro descargado", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "No se ha podido descargar el libro.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }


    //Este método guarda los libros que el usuario carga en la aplicación desde su dispositivol. Así la bd explorar tendrá más libros.
    public void guardarLibroBd(String ruta) {

        //Creo fichero mediante la ruta pasada por parámetro
        File epub = new File(ruta);
        Uri uri = Uri.fromFile(epub);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        //Nodo explorar y fichero a descargar.
        StorageReference referenceExplorar = reference.child("AAAExplorar");
        StorageReference referenceEpub = referenceExplorar.child(epub.getName());

        //Listo todos los elemtendo de la referencia explorar.
        referenceExplorar.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference libro : listResult.getItems()){
                    //Extraigo el nombre de la ruta.
                    String archivo = epub.getName();
                    int index = archivo.lastIndexOf('.');
                    String nombreArch = archivo.substring(0, index);

                    String archivo2 = libro.getName();
                    int index2 = archivo2.lastIndexOf('.');
                    String nombreArch2 = archivo2.substring(0, index2);

                    //Los igualo para saber si ya existe.
                    if(nombreArch.equals(nombreArch2)){
                        Log.d("Libro existe", "Existe");
                        return;
                    }
                }

                referenceEpub.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Libro guardado", "Hecho");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Libro no guardado", "Fallo");
                    }
                });


            }
        });

    }
}
