package com.example.epubook.controlador;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.epubook.R;
import com.example.epubook.fragments.ColeccionesFragment;
import com.example.epubook.fragments.LibrosFragment;
import com.example.epubook.modelo.ArchivoEpub;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.modelo.Libro;
import com.example.epubook.modelo.LibroExplorar;
import com.example.epubook.vista.EpubAdapter;
import com.example.epubook.vista.LibroAdapter;
import com.example.epubook.vista.LibroColeccAdapter;
import com.example.epubook.vista.PantallaPerfil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private ControlExplorar controlExplorar = new ControlExplorar(context);

    //Método que recoge los archivos epub y añade a la lista para mostrarlos en la clase ArchivosEpub.
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

                        ArchivoEpub nuevoArchivo = new ArchivoEpub(nombre, uri, tamanio, fecha, false, false);
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

    public void aniadirArchivoEpub(String ruta, Activity activity, ArchivoEpub archivoEpub, EpubAdapter epubAdapter){

        //Creo fichero mediante la ruta pasada por parámetro
        File epub = new File(ruta);
        Uri uri = Uri.fromFile(epub);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("users").child(uidUsuario);


        //Nodo usuario, donde guardaré los ficheros epub de cada usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis libros y epub.
        StorageReference referenceLibros = referenceUsuario.child("misLibros");
        StorageReference referenceEpub = referenceLibros.child(epub.getName());

        controlExplorar.guardarLibroBd(ruta);

        //Compruebo que no seleccionen el mismo fichero.
        referenceLibros.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
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
                        Toast.makeText(activity, "El fichero ya existe en la biblioteca.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //Primero, se obtendrá y guardará el fichero en el nodo mis libros.
                referenceEpub.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        archivoEpub.setDescargando(false);
                        archivoEpub.setGuardado(true);
                        epubAdapter.notifyDataSetChanged();

                        String archivo = epub.getName();
                        int index = archivo.lastIndexOf('.');
                        String nombreArch = archivo.substring(0, index);

                        refUser.child("historial").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    List<String> listaAcc = (List<String>) snapshot.getValue();

                                    listaAcc.add("Has añadido "+ nombreArch+ " a tus libros.");
                                    refUser.child("historial").setValue(listaAcc);
                                    Toast.makeText(activity, "Libro guardado", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(activity, "No se ha podido descargar el fichero.", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    public void obtenerMisLibros(LibroAdapter libroAdapter, List<Libro> listaLibros, ProgressBar progressBar, TextView noLibros, LibrosFragment librosFragment){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user!=null){

            String uid = user.getUid();

            //Obtengo referencia de mis libros.
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReference();
            StorageReference referenceMisLibros = reference.child(uid).child("misLibros");

            //Recorro los libros encontrados.
            referenceMisLibros.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    noLibros.setVisibility(View.VISIBLE);
                    listaLibros.clear();

                    //Obtengo los archivos epub.
                    for(StorageReference epub : listResult.getItems()){
                        try {
                            noLibros.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);

                            //Hago la referencia al archivo.
                            StorageReference referenceEpub = referenceMisLibros.child(epub.getName());

                            //Lo guardo en cache.
                            File archivoCache = cargarCacheEpub(epub.getName(), librosFragment.getActivity());

                            //Compruebo si ya se ha guardado en cache, si es así los muestro.
                            if(existeEpub(epub.getName(), librosFragment.getActivity())){
                                String ruta = archivoCache.getAbsolutePath();
                                List<Libro> libros = mostrarMisLibros(ruta);

                                listaLibros.addAll(libros);
                                libroAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }else{
                                //Si no es así, creo un archivo temporal que usaré para guardarlo en cache.
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
            String autor;

            if(!autores.isEmpty()){
                Author nombreAutor = autores.get(0);
                autor = nombreAutor.getFirstname() + " " +nombreAutor.getLastname();

            }else {
                autor = "";
            }

            Resource portada = libroEpub.getCoverImage();


           if(portada == null){
               Bitmap p = obtenerPortada(titulo);
               Libro libro = new Libro(titulo, autor, p, ruta, false);
               listaLibros.add(libro);
           }else{
               String idPortada = portada.getId();
               Resource portadaLibro = libroEpub.getResources().getById(idPortada);
               portadaMisLibros(portadaLibro, titulo, autor, ruta, listaLibros);
           }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return listaLibros;
    }

    public List<LibroExplorar> mostrarLibrosExp(String ruta){
        List<LibroExplorar> listaLibros = new ArrayList<>();

        try {
            //Leer archivo epub.
            File archivoEpub = new File(ruta);

            EpubReader reader = new EpubReader();
            Book libroEpub = reader.readEpub(new FileInputStream(archivoEpub));

            Metadata metadata = libroEpub.getMetadata();

            String t = metadata.getFirstTitle();
            String titulo="";

            if(!t.isEmpty()){
                titulo = t;
            }

            List<Author> autores = metadata.getAuthors();
            String autor;

            if(!autores.isEmpty()){
                Author nombreAutor = autores.get(0);
                autor = nombreAutor.getFirstname() + " " +nombreAutor.getLastname();

            }else {
                autor = "";
            }

            Resource cover = libroEpub.getCoverImage();

            if(cover == null){
                Bitmap p = obtenerPortada(titulo);
                LibroExplorar libro = new LibroExplorar(titulo, autor, p, ruta, false, false, 0);
                listaLibros.add(libro);
            }else{
                String idPortada = libroEpub.getCoverImage().getId();
                Resource portadaLibro = libroEpub.getResources().getById(idPortada);
                portadaLibro(portadaLibro, titulo, autor, ruta, listaLibros);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return listaLibros;
    }

    //Metodo que crea portada si no se encuentra a partir del titulo.
    public Bitmap obtenerPortada(String titulo){
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

        return bitmap;
    }

    public void portadaLibro(Resource portada, String titulo, String autor, String ruta, List<LibroExplorar> listaLibros) {

        try {
            byte[] portadaByte = portada.getData();
            Bitmap portadaBm = BitmapFactory.decodeByteArray(portadaByte, 0, portadaByte.length);
            LibroExplorar libro = new LibroExplorar(titulo, autor, portadaBm, ruta, false, false, 0);
            listaLibros.add(libro);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void portadaMisLibros(Resource portada, String titulo, String autor, String ruta, List<Libro> listaLibros) {

        try {
            byte[] portadaByte = portada.getData();
            Bitmap portadaBm = BitmapFactory.decodeByteArray(portadaByte, 0, portadaByte.length);
            Libro libro = new Libro(titulo, autor, portadaBm, ruta, false);
            listaLibros.add(libro);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //En caso de guardar un nuevo fichero epub, se guarda en el cache.
    public void guardarEpubCache(String nombre, File archivo, Activity activity){

        try {
            //Fichero creado a patir de la ruta donde se alamacerá y nombre pasado por parámetro.
            File ruta = activity.getApplicationContext().getCacheDir();
            File fichero = new File(ruta, nombre);

            //Leo el fichero pasado por parámetro y se escribe en el nuevo fichero creado.
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
    public boolean existeEpub(String nombre, Activity activity){
        File ruta = activity.getApplicationContext().getCacheDir();
        File fichero = new File(ruta, nombre);
        return fichero.exists();
    }

    //Método para cargar ficheros existentes desde cache.
    public File cargarCacheEpub(String nombre, Activity activity){
        File ruta = activity.getApplicationContext().getCacheDir();
        return new File(ruta, nombre);

    }

    //Método para eliminar libro de mis libros.
    public void eliminarEpub(int pos, List<Libro> listalibros, LibroAdapter libroAdapter, View view, TextView noLibros) {
        Libro libro = listalibros.get(pos);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        //Referencia del archivo que elimaré.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        File epub = new File(libro.getRuta());
        StorageReference referenceLibro = storage.getReference().child(uid).child("misLibros").child(epub.getName());

       referenceLibro.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   listalibros.remove(pos);
                   libroAdapter.notifyItemRemoved(pos);
                   libroAdapter.notifyDataSetChanged();

                   //Notifico a historial.
                   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                   String uid = user.getUid();
                   DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

                   reference.child("historial").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.exists()){
                               List<String> listaAcc = (List<String>) snapshot.getValue();

                               listaAcc.add("Has eliminado "+ libro.getTitulo()+ " de tus libros.");
                               reference.child("historial").setValue(listaAcc);
                               Toast.makeText(view.getContext(), libro.getTitulo()+" se ha eliminado.", Toast.LENGTH_SHORT).show();
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {
                       }
                   });

                   if(listalibros.size() == 0){
                       noLibros.setVisibility(View.VISIBLE);
                   }else{
                       noLibros.setVisibility(View.GONE);
                   }
               }
           }

       });
    }

    //Método para eliminar libro de una colección.
    public void eliminarLibroC(int pos, List<Libro> listalibros, LibroColeccAdapter libroColeccAdapter, Coleccion coleccion, ColeccionesFragment coleccionesFragment) {
        //Obtengo libro con pos.
        Libro libro = listalibros.get(pos);

        //Accedo a firebase storage.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Creo fichero con la ruta del libro obtenido.
        File epub = new File(libro.getRuta());

        //Extraigo el nombre de la ruta.
        String archivo = epub.getName();
        int index = archivo.lastIndexOf('.');
        String nombreArch = archivo.substring(0, index);

        //Referencia del archivo que se eliminará.
        StorageReference referenceLibro = storage.getReference().child(uid).child("misColecciones").child(coleccion.getNombre()).child(nombreArch+".epub");

        //Elimino el archivo epub de la colección.
        referenceLibro.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listalibros.remove(pos);
                libroColeccAdapter.notifyItemRemoved(pos);
                libroColeccAdapter.notifyDataSetChanged();

                //Notifico a historial.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

                reference.child("historial").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            List<String> listaAcc = (List<String>) snapshot.getValue();

                            listaAcc.add("Has eliminado "+ nombreArch+ " de "+coleccion.getNombre()+".");
                            reference.child("historial").setValue(listaAcc);
                            Toast.makeText(coleccionesFragment.getContext(), "Libro eliminado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }


}
