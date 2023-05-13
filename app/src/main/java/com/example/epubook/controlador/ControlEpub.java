package com.example.epubook.controlador;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.epubook.R;
import com.example.epubook.fragments.LibrosFragment;
import com.example.epubook.modelo.ArchivoEpub;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.EpubAdapter;
import com.example.epubook.vista.LibroAdapter;
import com.example.epubook.vista.PantallaInicio;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

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
    @TargetApi(Build.VERSION_CODES.O)
    public void aniadirArchivoEpub(String ruta, Activity activity){
        //Creo fichero mediante la ruta pasada por parámetro
        File epub = new File(ruta);
        Uri uri = Uri.fromFile(epub);

        //Creo nodo epubs donde guardaré los ficheros de cada ususario en firestorage.
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis libros y epub.
        StorageReference referenceLibros = referenceUsuario.child("misLibros/");
        StorageReference referenceEpub = referenceLibros.child(epub.getName());

        //Control de fichero existente.
        if(epub.exists()){
            //Primero, se obtendrá y guardará el fichero en el nodo mis libros.
            referenceEpub.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Libro guardado", Toast.LENGTH_SHORT).show();//Cambiar por dialog.
                    Intent intent = new Intent(context, PantallaInicio.class);
                    context.startActivity(intent);
                    activity.finish();

                }
            });
        }else{
            Toast.makeText(context, "NO", Toast.LENGTH_SHORT).show();
        }

    }

    public void obtenerMisLibros(LibroAdapter libroAdapter, List<Libro> listaLibros, ProgressBar progressBar, EditText buscar, TextView noLibros, LibrosFragment librosFragment){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user!=null){

            String uid = user.getUid();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReference();
            StorageReference referenceMisLibros = reference.child(uid).child("misLibros");

            referenceMisLibros.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    noLibros.setVisibility(View.VISIBLE);
                    listaLibros.clear();
                    for(StorageReference epub : listResult.getItems()){
                        try {
                            noLibros.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);

                            StorageReference referenceEpub = referenceMisLibros.child(epub.getName());

                            File archivoCache = cargarCacheEpub(epub.getName(), librosFragment.getActivity());
                            if(existeEpub(epub.getName(), librosFragment.getActivity())){
                                String ruta = archivoCache.getAbsolutePath();
                                List<Libro> libros = mostrarMisLibros(ruta);

                                listaLibros.addAll(libros);
                                libroAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                buscar.setVisibility(View.VISIBLE);
                            }else{
                                File epubLocal = File.createTempFile(epub.getName(), "epub");

                                referenceEpub.getFile(epubLocal).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        String ruta = epubLocal.getAbsolutePath();
                                        guardarEpubCache(epub.getName(), epubLocal, librosFragment.getActivity());
                                        List<Libro> libros = mostrarMisLibros(ruta);

                                        listaLibros.addAll(libros);
                                        libroAdapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                        buscar.setVisibility(View.VISIBLE);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        System.out.println("FALLO");
                                    }
                                });

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }


                }
            });

        }


    }

    public List<Libro> mostrarMisLibros(String ruta){
        List<Libro> listaLibros = new ArrayList<>();

        try {
            //Leer archivo epub.
            File archivoEpub = new File(ruta);

            EpubReader reader = new EpubReader();
            Book libroEpub = reader.readEpub(new FileInputStream(archivoEpub));

            Metadata metadata = libroEpub.getMetadata();

            String titulo = metadata.getFirstTitle();

            List<Author> autores = metadata.getAuthors();
            Author nombreAutor = autores.get(0);
            String autor = nombreAutor.getFirstname() + " " +nombreAutor.getLastname();

            Resource portada = libroEpub.getResources().getById("cover");

            //No encuentra la portada. Cargo una portada por defecto.
            if(portada == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.no_portada, null);

                //Le pongo a la portada que se generará por defecto, el título del libro.
                TextView tituloPortada = view.findViewById(R.id.txtNombrePortada);
                tituloPortada.setText(titulo);

                //Tamaño de la vista.
                int w = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
                int h = View.MeasureSpec.makeMeasureSpec(1280, View.MeasureSpec.EXACTLY);
                view.measure(w, h);
                view.layout(0, 0, 800, 1280);

                //A partir de la vista, obtengo el bitmap.
                RelativeLayout relativeLayout = view.findViewById(R.id.vistaNoportada);
                relativeLayout.setDrawingCacheEnabled(true);
                relativeLayout.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(relativeLayout.getDrawingCache());
                relativeLayout.setDrawingCacheEnabled(false);

                Libro libro = new Libro(titulo, autor, bitmap, ruta);
                listaLibros.add(libro);


            }else{
                //Obtengo la portada.
                byte[] portadaByte = portada.getData();
                Bitmap portadaBm = BitmapFactory.decodeByteArray(portadaByte, 0, portadaByte.length);

                Libro libro = new Libro(titulo, autor, portadaBm, ruta);
                listaLibros.add(libro);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return listaLibros;
    }

    //En caso de guardar un nuevo fichero epub, se guarda en el cache.
    private void guardarEpubCache(String nombre, File archivo, Activity activity){

        try {

            File ruta = activity.getApplicationContext().getCacheDir();
            File fichero = new File(ruta, nombre);

            InputStream is = new FileInputStream(archivo);
            OutputStream os = new FileOutputStream(fichero);
            byte[] buf = new byte[1024];
            int tam;
            while((tam = is.read(buf))>0){
                os.write(buf, 0, tam);
            }

            is.close();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Método para comprobar que mi fichero existe.
    private boolean existeEpub(String nombre, Activity activity){
        File ruta = activity.getApplicationContext().getCacheDir();
        File fichero = new File(ruta, nombre);
        return fichero.exists();
    }

    //Método para cargar ficheros existentes desde cache.
    private File cargarCacheEpub(String nombre, Activity activity){
        File ruta = activity.getApplicationContext().getCacheDir();
        return new File(ruta, nombre);
    }

}
