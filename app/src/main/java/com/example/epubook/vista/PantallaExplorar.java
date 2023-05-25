package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlExplorar;
import com.example.epubook.controlador.ControlUsuario;
import com.example.epubook.modelo.Libro;
import com.example.epubook.modelo.LibroExplorar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PantallaExplorar extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu, fondoExpl;
    private LinearLayout espacio, fondorosa;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion, escribir, explorar;

    private EditText buscarLibros;

    private List<LibroExplorar> listaLibros = new ArrayList<>();
    private List<LibroExplorar> listaLibCat = new ArrayList<>();
    private List<String> listaCategorias = new ArrayList<>();
    private ExpCabeceraAdapter cabeceraAdapter;
    private CategoriaAdapter categoriaAdapter;
    private LibroCatAdapter libroCatAdapter;
    private RecyclerView recyclerCabecera, recyclerCat, recyclerLibroCat;
    private boolean unClick = true;
    final  int dobleClickTiempo = 700;
    Handler handler = new Handler();
    Runnable runnable;

    ControlExplorar controlExplorar = new ControlExplorar(PantallaExplorar.this);
    ControlUsuario controlUsuario = new ControlUsuario(PantallaExplorar.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.activity_pantalla_explorar);

        //Variables necesarias para poder navegar entre actividades.
        drawerLayout = findViewById(R.id.dsp_contenido);
        menu = findViewById(R.id.menu);
        inicio = findViewById(R.id.inicio);
        perfil = findViewById(R.id.perfil);
        ajustes = findViewById(R.id.ajustes);
        cerrarSesion = findViewById(R.id.cerrarSesion);
        escribir = findViewById(R.id.escribir);
        explorar = findViewById(R.id.explorar);

        fondoExpl = findViewById(R.id.imgExpl);
        buscarLibros = findViewById(R.id.buscarLibExpl);
        espacio = findViewById(R.id.espacio);
        fondorosa = findViewById(R.id.fondoRosa);

        //Recycler cabecera.
        recyclerCabecera = findViewById(R.id.recyclerCabExp);
        cabeceraAdapter = new ExpCabeceraAdapter(listaLibros);
        recyclerCabecera.setAdapter(cabeceraAdapter);
        recyclerCabecera.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        controlExplorar.mostrarLibrosExp(listaLibros, cabeceraAdapter, PantallaExplorar.this);

        //Recycler categorias.
        recyclerCat = findViewById(R.id.recyclerCategoria);
        categoriaAdapter = new CategoriaAdapter(listaCategorias);
        recyclerCat.setAdapter(categoriaAdapter);
        recyclerCat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        controlExplorar.mostrarCategorias(listaCategorias, categoriaAdapter);


        recyclerLibroCat = findViewById(R.id.recyclerLibCat);
        recyclerLibroCat.setVisibility(View.GONE);
        fondoExpl.setVisibility(View.VISIBLE);

        buscarNuevosLibros();

        categoriaAdapter.setOnCatListener(new CategoriaAdapter.OnCatClick() {
            @Override
            public void onCatClick(int pos) {

                if(unClick){

                    unClick = false;

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            unClick = true;
                        }
                    };

                    //Tiempo que no funcionará el click.
                    handler.postDelayed(runnable, dobleClickTiempo);

                    recyclerLibroCat.setVisibility(View.VISIBLE);
                    fondoExpl.setVisibility(View.GONE);

                    //Recycler libros según categoría.
                    libroCatAdapter = new LibroCatAdapter(listaLibCat);
                    recyclerLibroCat.setAdapter(libroCatAdapter);
                    recyclerLibroCat.setLayoutManager(new LinearLayoutManager(PantallaExplorar.this));

                    controlExplorar.mostrarLibrosCat(listaLibCat, libroCatAdapter, listaCategorias.get(pos), PantallaExplorar.this);


                }

            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaExplorar.this, PantallaInicio.class);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaExplorar.this, PantallaPerfil.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaExplorar.this, PantallaAjustes.class);
            }
        });

        escribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaExplorar.this, PantallaEscribir.class);
            }
        });

        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PantallaExplorar.this, "Has cerrarado sesión", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(PantallaExplorar.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

    }


    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public void buscarNuevosLibros(){
        buscarLibros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) buscarLibros.getLayoutParams();

                if(charSequence.toString().isEmpty()){
                    fondoExpl.setVisibility(View.VISIBLE);
                    recyclerCat.setVisibility(View.VISIBLE);
                    recyclerCabecera.setVisibility(View.VISIBLE);
                    recyclerLibroCat.setVisibility(View.INVISIBLE);
                    espacio.setVisibility(View.INVISIBLE);
                    fondorosa.setVisibility(View.VISIBLE);
                    layoutParams.setMargins(layoutParams.leftMargin, 670, layoutParams.rightMargin, layoutParams.bottomMargin);
                    buscarLibros.setLayoutParams(layoutParams);

                }else{
                    fondoExpl.setVisibility(View.GONE);
                    recyclerCat.setVisibility(View.GONE);
                    recyclerCabecera.setVisibility(View.INVISIBLE);
                    recyclerLibroCat.setVisibility(View.VISIBLE);
                    fondorosa.setVisibility(View.GONE);
                    espacio.setVisibility(View.VISIBLE);
                    layoutParams.setMargins(layoutParams.leftMargin, 80, layoutParams.rightMargin, layoutParams.bottomMargin);
                    buscarLibros.setLayoutParams(layoutParams);

                    libroCatAdapter = new LibroCatAdapter(listaLibros);
                    recyclerLibroCat.setAdapter(libroCatAdapter);
                    recyclerLibroCat.setLayoutManager(new LinearLayoutManager(PantallaExplorar.this));
                    libroCatAdapter.getFilter().filter(charSequence);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}