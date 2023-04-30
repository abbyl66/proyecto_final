package com.example.epubook.vista;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
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

public class PantallaInicio extends AppCompatActivity {

    private FloatingActionButton botonAniadir;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio);

        bottomNavigationView = findViewById(R.id.vista_bajo);
        botonAniadir = findViewById(R.id.bt_menuabj);
        drawerLayout = findViewById(R.id.dsp_contenido);

        NavigationView navigationView = findViewById(R.id.dsp_nav);
        Toolbar toolbar = findViewById(R.id.dsp_toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.desc1,R.string.desc1 );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.dsp_bajo, new LibrosFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inicio);
        }

        replaceFragment(new LibrosFragment());

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

        botonAniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoAniadir();
            }
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dsp_bajo, fragment);
        fragmentTransaction.commit();

    }

    private void mostrarDialogoAniadir(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_aniadir);

        LinearLayout nuevoLibro = dialog.findViewById(R.id.an_libro);
        LinearLayout nuevaColecc = dialog.findViewById(R.id.an_coleccion);
        ImageView botonCancelar = dialog.findViewById(R.id.an_cancelar);

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