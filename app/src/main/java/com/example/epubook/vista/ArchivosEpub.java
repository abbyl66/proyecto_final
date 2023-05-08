package com.example.epubook.vista;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.modelo.ArchivoEpub;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchivosEpub extends AppCompatActivity {

    RecyclerView recyclerView;
    EpubAdapter epubAdapter;

    EditText buscarArchivo;
    ImageView atras;

    TextView noEpub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.activity_archivos_epub);

        buscarArchivo = findViewById(R.id.buscarArchivo);
        atras = findViewById(R.id.atrasArch);

        noEpub = findViewById(R.id.noEpub);

        burcarArch();

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Compruebo que el usuario haya dado acceso a la lectura de ficheros.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
            cargarEpub();
        }else{
            //Dialogo diciendo que es necesario que dé acceso a ficheros. Si dice que si, manda intent, si dice que no, dialog diciendo que no se mostrarán los libros.
            dialogoConfirmacion();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
                onResume();
                cargarEpub();
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEpub();
    }

    private void dialogoConfirmacion(){
        ConstraintLayout confirmacion = findViewById(R.id.dialogoConfirm);
        View view = LayoutInflater.from(ArchivosEpub.this).inflate(R.layout.dialogo_confirmacion, confirmacion);

        TextView tituloDialog, infoDialog;
        Button cancelarDialog, aceptarDialog;

        tituloDialog = view.findViewById(R.id.confirmTitulo);
        infoDialog = view.findViewById(R.id.infoConfirm);
        cancelarDialog = view.findViewById(R.id.btcancelar);
        aceptarDialog = view.findViewById(R.id.btaceptar);

        AlertDialog.Builder builder = new AlertDialog.Builder(ArchivosEpub.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        tituloDialog.setText("Permisos");
        infoDialog.setText("Debe dar permisos de acceso para poder mostrar sus archivos.");
        cancelarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Toast.makeText(ArchivosEpub.this, "No podemos mostrar sus archivos epub.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        aceptarDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


    @TargetApi(Build.VERSION_CODES.R)
    private void cargarEpub(){

        //Creo una lista de archivos donde se recogerán los archivos epub.
        List<ArchivoEpub> archivosEpub = new ArrayList<>();

        //Accedo al almacenamiento del dispositivo.
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        StorageVolume storageVolume = storageVolumes.get(0);

        //Paso el directorio a través de su ruta.
        File directorio = new File(storageVolume.getDirectory().getAbsolutePath());
        mostrarEpub(directorio, archivosEpub);

        //Doy formato al layout.
        recyclerView = findViewById(R.id.archRecyclerView);

        //Paso los archivos recogidos al adapter para que se muestren en el recyclerview.
        epubAdapter = new EpubAdapter(archivosEpub);

        epubAdapter.setOnItemClickListener(new EpubAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                aniadirArchivoEpub(archivosEpub.get(pos).getUri());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(epubAdapter);

    }

    public void aniadirArchivoEpub(String ruta){
        File epub = new File(ruta);
        Uri uri = Uri.fromFile(epub);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference referenceUsuario = reference.child("epubs").child(uidUsuario).child(uidUsuario+"_archivosEpub");

        if(epub.exists()){
            referenceUsuario.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(this, "SI", Toast.LENGTH_SHORT).show();
            });
        }else{
            Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show();
        }

    }

    //Método que recoge los archivos epub.
    private void mostrarEpub(File direc, List<ArchivoEpub> aEpub){

        File[] archivos = direc.listFiles();
        if(archivos != null){
            for (File file : archivos){
                //Si es un directorio vuelve a abrirlo.
                if(file.isDirectory()){
                    mostrarEpub(file, aEpub);
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

    public void burcarArch(){
        buscarArchivo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                epubAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}