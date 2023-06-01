package com.example.epubook.controlador;

import android.os.AsyncTask;

import com.example.epubook.modelo.LibroExplorar;
import com.example.epubook.vista.ExpCabeceraAdapter;
import com.example.epubook.vista.PantallaExplorar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Clase para cargar los libros de explorar.
public class CargarLibrosAsync extends AsyncTask<List<StorageReference>, Void, List<LibroExplorar>> {

    private PantallaExplorar pantallaExplorar;
    private ControlEpub controlEpub;
    private ExpCabeceraAdapter expCabeceraAdapter;
    private List<LibroExplorar> listaLibros, listaCont;

    public CargarLibrosAsync(PantallaExplorar pantallaExplorar, ControlEpub controlEpub, ExpCabeceraAdapter expCabeceraAdapter, List<LibroExplorar> listaLibros) {
        this.pantallaExplorar = pantallaExplorar;
        this.controlEpub = controlEpub;
        this.expCabeceraAdapter = expCabeceraAdapter;
        this.listaLibros = listaLibros;
        this.listaCont = new ArrayList<>();
    }

    //Obtengo los libros de la bd.
    @Override
    protected List<LibroExplorar> doInBackground(List<StorageReference>... storageReferences) {

        //Voy hasta el nodo explorar que es donde se encuentran los libros.
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();
        StorageReference referenceExpl = reference.child("AAAExplorar");

        final int librosTotal = storageReferences[0].size();
        final int[] contador = {0};

        //Recorro los libros.
        for(StorageReference epub : storageReferences[0]){
            try{

                //A patir del nodo explorar, consigo el del fichero que obtendré.
                StorageReference referenceArchivo = referenceExpl.child(epub.getName());
                //Creo un fichero temporar para guardarlo.
                File archivoTemp = File.createTempFile(epub.getName(), "epub");
                //Obtengo el fichero y lo añado a la lista contador.
                referenceArchivo.getFile(archivoTemp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        List<LibroExplorar> libros =   controlEpub.mostrarLibrosExp(archivoTemp.getAbsolutePath());
                        listaCont.addAll(libros);

                        synchronized (contador){
                            contador[0]++;
                            if(contador[0]==librosTotal){
                                publishProgress();
                            }
                        }
                    }
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return listaCont;

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        pantallaExplorar.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listaLibros.addAll(listaCont);
                expCabeceraAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPostExecute(List<LibroExplorar> libroExplorars) {
        super.onPostExecute(libroExplorars);
        listaLibros.addAll(libroExplorars);
        expCabeceraAdapter.notifyDataSetChanged();
    }
}
