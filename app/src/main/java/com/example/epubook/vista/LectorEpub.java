package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.epubook.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.domain.TOCReference;

public class LectorEpub extends AppCompatActivity {

    private Book libro;
    private WebView contenidoEpub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_epub);


        contenidoEpub = findViewById(R.id.txtContenidoEpub);


        String ruta = getIntent().getStringExtra("ruta");
        abrirLecturaEpub(ruta);


    }

    public void abrirLecturaEpub(String ruta){
        try {

            File file = new File(ruta);
            libro = (new EpubReader()).readEpub(new FileInputStream(file));

            Spine spine = libro.getSpine();
            List<SpineReference> spineReferences = spine.getSpineReferences();
            StringBuilder contenidoLibro = new StringBuilder();

            for(SpineReference reference : spineReferences){
                try{
                    Resource resource = reference.getResource();
                    String contenido = new String(resource.getData());
                    Document document = Jsoup.parse(contenido);
                    contenido = document.html();
                    contenidoLibro.append(contenido);
                }catch (IOException e){
                    e.printStackTrace();
                }

                contenidoEpub.getSettings().setJavaScriptEnabled(true);
                contenidoEpub.loadDataWithBaseURL(null, contenidoLibro.toString(), "text/html", "UTF-8", null);


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}