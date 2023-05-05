package com.example.epubook.vista;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.epubook.R;
import com.example.epubook.modelo.ArchivoEpub;

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

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivos_epub);

        buscarArchivo = findViewById(R.id.buscarArchivo);
        atras = findViewById(R.id.atrasArch);

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
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
                cargarEpub();
            }else{
                //Dialogo diciendo que no se mostrarán los libros.
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(epubAdapter);

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
                        //Obtengo datos del fichero.
                        String nombre = file.getName();
                        double tamanio = file.length();
                        long fechalong = file.lastModified();

                        //Formato fecha.
                        Date fecha = new Date(fechalong);
                        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");

                        //Creo objeto y lo añado a mi lista de archivos.
                        ArchivoEpub nuevoArchivo = new ArchivoEpub(nombre, tamanio, fecha);
                        aEpub.add(nuevoArchivo);
                    }
                }
            }
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