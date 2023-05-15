package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.example.epubook.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;

public class LectorEpub extends AppCompatActivity {

    private WebView webView;
    private Book libro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_epub);

        webView = findViewById(R.id.txtLecuraEpub);

        String ruta = getIntent().getStringExtra("ruta");
        abrirLecturaEpub(ruta);
    }

    public void abrirLecturaEpub(String ruta){
        try {

            File file = new File(ruta);
            libro = (new EpubReader()).readEpub(new FileInputStream(file));

            if(libro != null){
                System.out.println("CORRECTO");
            }else{
                System.out.println("INCORRECTO");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Spine spine = libro.getSpine();
        List<SpineReference> spineReferences = spine.getSpineReferences();
        StringBuilder contenidoLibro = new StringBuilder();

        for(SpineReference reference : spineReferences){
            try{
                Resource resource = reference.getResource();
                String contenido = new String(resource.getData());
                //Log.d("contenido", contenido);
                contenidoLibro.append(contenido);
            }catch (IOException e){
                e.printStackTrace();
            }

            webView.loadDataWithBaseURL(null, contenidoLibro.toString(), "text/html", "UTF-8", null);


        }

    }

}