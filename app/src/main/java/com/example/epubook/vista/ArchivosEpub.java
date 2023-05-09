package com.example.epubook.vista;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.controlador.ControlEpub;
import com.example.epubook.modelo.ArchivoEpub;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchivosEpub extends AppCompatActivity {

    RecyclerView recyclerView;
    EpubAdapter epubAdapter;

    EditText buscarArchivo;
    ImageView atras;

    TextView noEpub;

    ControlDialogos controlDialogos = new ControlDialogos(ArchivosEpub.this);
    ControlEpub controlEpub = new ControlEpub(ArchivosEpub.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.activity_archivos_epub);

        View miVista = findViewById(R.id.vistaArchivosEpub);

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
            //Dialogo para comprobar que el usuario da los permisos necesarios para mostrar los archivos.
            controlDialogos.dialogoConfirmacion(miVista, ArchivosEpub.this);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
                onResume();
                cargarEpub();
            }

        }

    }

    //Método para cargar ficheros epub del almacenamiento del usuario.
    @TargetApi(Build.VERSION_CODES.R)
    public void cargarEpub(){

        //Creo una lista de archivos donde se recogerán los archivos epub.
        List<ArchivoEpub> archivosEpub = new ArrayList<>();

        //Accedo al almacenamiento del dispositivo.
        StorageManager storageManager = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        StorageVolume storageVolume = storageVolumes.get(0);

        //Paso el directorio a través de su ruta.
        File directorio = new File(storageVolume.getDirectory().getAbsolutePath());
        controlEpub.mostrarEpub(directorio, archivosEpub, noEpub);

        //Doy formato al layout.
        recyclerView = findViewById(R.id.archRecyclerView);

        //Paso los archivos recogidos al adapter para que se muestren en el recyclerview.
        epubAdapter = new EpubAdapter(archivosEpub);

        epubAdapter.setOnItemClickListener(new EpubAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                controlEpub.aniadirArchivoEpub(archivosEpub.get(pos).getUri());
            }
        });

        //Doy formato.
        recyclerView.setLayoutManager(new LinearLayoutManager(ArchivosEpub.this));
        recyclerView.setAdapter(epubAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarEpub();
    }



    //Método para buscar archivos desde la barra de búsqueda.
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