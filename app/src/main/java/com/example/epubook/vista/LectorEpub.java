package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;

public class LectorEpub extends AppCompatActivity {

    private Book libro;
    private WebView contenidoEpub;
    private LinearLayout contAjustes;
    private ConstraintLayout coloresFondo;
    private TextView cerrarFondo;
    private ImageView fondonegro, fondoblanco, fondocrema, fondogris;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_epub);

        contenidoEpub = findViewById(R.id.webViewEpub);
        contAjustes = findViewById(R.id.contAjustes);
        coloresFondo = findViewById(R.id.ajusteswv);
        cerrarFondo = findViewById(R.id.cerrarColores);

        fondoblanco = findViewById(R.id.fondoBlanco);
        fondocrema = findViewById(R.id.fondoCrema);
        fondogris = findViewById(R.id.fondoGris);
        fondonegro = findViewById(R.id.fondoNegro);


        String ruta = getIntent().getStringExtra("ruta");
        abrirLecturaEpub(ruta);

        contenidoEpub.setOnTouchListener(new View.OnTouchListener() {

            private float x, y;
            private static final int distancia = 150;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = motionEvent.getX();
                        y = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float fx = motionEvent.getX();
                        float fy = motionEvent.getY();
                        float dx = Math.abs(fx - x);
                        float dy = Math.abs(fy - y);

                        if (dx < distancia && dy < distancia) {
                            if (contAjustes.getVisibility() == View.VISIBLE) {
                                contAjustes.setVisibility(View.GONE);
                            } else if (contAjustes.getVisibility() == View.GONE) {
                                contAjustes.setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                }
                return false;
            }
        });

        contAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contAjustes.setVisibility(View.GONE);
                coloresFondo.setVisibility(View.VISIBLE);
            }
        });

        cerrarFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coloresFondo.setVisibility(View.GONE);
                contAjustes.setVisibility(View.GONE);
            }
        });


    }

    public void abrirLecturaEpub(String ruta) {
        try {
            File file = new File(ruta);
            libro = (new EpubReader()).readEpub(new FileInputStream(file));

            String url = "file://" + file.getParent() + "/";

            Spine spine = libro.getSpine();
            List<SpineReference> spineReferences = spine.getSpineReferences();
            StringBuilder contenidoLibro = new StringBuilder();

            fondocrema.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contenidoEpub.setBackgroundColor(Color.parseColor("#EDE8E0"));
                    coloresFondo.setVisibility(View.GONE);

                    String css = "<style>body {color: black; }</style>";
                    contenidoLibro.append("<html><head>").append(css).append("</head><body>");

                    for (SpineReference reference : spineReferences) {
                        try {
                            Resource resource = reference.getResource();
                            String contenido = new String(resource.getData());
                            Document document = Jsoup.parse(contenido);
                            contenido = document.html();
                            contenidoLibro.append(contenido);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    contenidoLibro.append("</body></html>");

                    contenidoEpub.getSettings().setJavaScriptEnabled(true);
                    contenidoEpub.loadDataWithBaseURL(url, contenidoLibro.toString(), "text/html", "UTF-8", null);

                }
            });

            fondoblanco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contenidoEpub.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    coloresFondo.setVisibility(View.GONE);

                    String css = "<style>body {color: black; }</style>";
                    contenidoLibro.append("<html><head>").append(css).append("</head><body>");

                    for (SpineReference reference : spineReferences) {
                        try {
                            Resource resource = reference.getResource();
                            String contenido = new String(resource.getData());
                            Document document = Jsoup.parse(contenido);
                            contenido = document.html();
                            contenidoLibro.append(contenido);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    contenidoLibro.append("</body></html>");

                    contenidoEpub.getSettings().setJavaScriptEnabled(true);
                    contenidoEpub.loadDataWithBaseURL(url, contenidoLibro.toString(), "text/html", "UTF-8", null);


                }
            });

            fondonegro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contenidoEpub.setBackgroundColor(Color.parseColor("#000000"));
                    coloresFondo.setVisibility(View.GONE);

                    String css = "<style>body {color: white; }</style>";
                    contenidoLibro.append("<html><head>").append(css).append("</head><body>");

                    for (SpineReference reference : spineReferences) {
                        try {
                            Resource resource = reference.getResource();
                            String contenido = new String(resource.getData());
                            Document document = Jsoup.parse(contenido);
                            contenido = document.html();
                            contenidoLibro.append(contenido);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    contenidoLibro.append("</body></html>");

                    contenidoEpub.getSettings().setJavaScriptEnabled(true);
                    contenidoEpub.loadDataWithBaseURL(url, contenidoLibro.toString(), "text/html", "UTF-8", null);


                }
            });

            fondogris.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contenidoEpub.setBackgroundColor(Color.parseColor("#4D4D4D"));
                    coloresFondo.setVisibility(View.GONE);

                    String css = "<style>body {color: white; }</style>";
                    contenidoLibro.append("<html><head>").append(css).append("</head><body>");

                    for (SpineReference reference : spineReferences) {
                        try {
                            Resource resource = reference.getResource();
                            String contenido = new String(resource.getData());
                            Document document = Jsoup.parse(contenido);
                            contenido = document.html();
                            contenidoLibro.append(contenido);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    contenidoLibro.append("</body></html>");

                    contenidoEpub.getSettings().setJavaScriptEnabled(true);
                    contenidoEpub.loadDataWithBaseURL(url, contenidoLibro.toString(), "text/html", "UTF-8", null);


                }
            });

            for (SpineReference reference : spineReferences) {
                try {
                    Resource resource = reference.getResource();
                    String contenido = new String(resource.getData());
                    Document document = Jsoup.parse(contenido);
                    contenido = document.html();
                    contenidoLibro.append(contenido);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            contenidoLibro.append("</body></html>");

            contenidoEpub.getSettings().setJavaScriptEnabled(true);
            contenidoEpub.loadDataWithBaseURL(url, contenidoLibro.toString(), "text/html", "UTF-8", null);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}