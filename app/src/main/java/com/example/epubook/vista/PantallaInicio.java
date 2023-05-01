package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.fragments.ColeccionesFragment;
import com.example.epubook.fragments.LibrosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class PantallaInicio extends AppCompatActivity{

    private FloatingActionButton botonAniadir;
    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio);

        //Variables que usaré para el menú de abajo.
        bottomNavigationView = findViewById(R.id.vista_bajo);
        botonAniadir = findViewById(R.id.bt_menuabj);

        //Variables que usaré para fragment desplegable izquierdo.
        drawerLayout = findViewById(R.id.dsp_contenido);
        menu = findViewById(R.id.menu);
        inicio = findViewById(R.id.inicio);
        perfil = findViewById(R.id.perfil);
        ajustes = findViewById(R.id.ajustes);
        cerrarSesion = findViewById(R.id.cerrarSesion);

        //Al pulsar en menú, inicio, perfil, ajustes o cerrar sesión, se muestran sus pantallas o funciones.
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActivity(PantallaInicio.this, PantallaPerfil.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActivity(PantallaInicio.this, PantallaAjustes.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PantallaInicio.this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
            }
        });

        replaceFragment(new LibrosFragment());

        //Control al pulsar en el menú inferior.
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.mab_libros:
                    replaceFragment(new LibrosFragment());
                    break;
                case R.id.mab_coleccion:
                    replaceFragment(new ColeccionesFragment());
                    break;
            }

            return true;
        });

        //Botón añadir del menú inferior.
        botonAniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoAniadir();
            }
        });

    }

    //Método para abrir drawer del fragment desplegable.
    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    //Método para cerrar drawer del fragment desplegable.
    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    //Método para abrir y mostrar un activity, pantalla.
    public static void abrirActivity (Activity activity, Class activity2){
        Intent intent = new Intent(activity, activity2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    //Menú inferior.
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dsp_bajo, fragment);
        fragmentTransaction.commit();

    }

    //Al pulsar sobre añadir nuevo en el menú inferior. Muestra su diálogo.
    private void mostrarDialogoAniadir(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_aniadir);

        LinearLayout nuevoLibro = dialog.findViewById(R.id.an_libro);
        LinearLayout nuevaColecc = dialog.findViewById(R.id.an_coleccion);
        ImageView botonCancelar = dialog.findViewById(R.id.an_cancelar);

        //Opciones que se pueden realizar desde el diálogo.
        nuevoLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(PantallaInicio.this, "Añadir libro", Toast.LENGTH_SHORT).show();

            }
        });

        nuevaColecc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(PantallaInicio.this, "Añadir colección", Toast.LENGTH_SHORT).show();
            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.animacionDialogo;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}