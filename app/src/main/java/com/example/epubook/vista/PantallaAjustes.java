package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.controlador.ControlUsuario;
import com.example.epubook.fragments.ColeccionesFragment;
import com.example.epubook.fragments.LibrosFragment;
import com.example.epubook.modelo.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PantallaAjustes extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion, explorar, datospers, cambiarContr, informacion;
    ControlUsuario controlUsuario = new ControlUsuario(PantallaAjustes.this);
    ControlDialogos controlDialogos = new ControlDialogos(PantallaAjustes.this);

    private SwitchCompat cambiarTema;
    boolean temaOscuro;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.activity_pantalla_ajustes);

        cambiarTema = findViewById(R.id.cambiarModo);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        temaOscuro = sharedPreferences.getBoolean("temaOscuro", false);

        if(temaOscuro){
            cambiarTema.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        cambiarTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(temaOscuro){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("temaOscuro", false);

                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("temaOscuro", true);
                }

                editor.apply();

            }
        });

        drawerLayout = findViewById(R.id.dsp_contenido);
        menu = findViewById(R.id.menu);
        inicio = findViewById(R.id.inicio);
        perfil = findViewById(R.id.perfil);
        ajustes = findViewById(R.id.ajustes);
        cerrarSesion = findViewById(R.id.cerrarSesion);
        explorar = findViewById(R.id.explorar);
        datospers = findViewById(R.id.datosPers);
        cambiarContr = findViewById(R.id.cambiarContr);
        informacion = findViewById(R.id.infoAjustes);


        datospers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaAjustes.this, DatosPersonales.class);
                startActivity(intent);
            }
        });

        cambiarContr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaAjustes.this, CambiarContrasenia.class);
                startActivity(intent);
            }
        });

        informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaAjustes.this, PantallaInformacion.class);
                startActivity(intent);
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
                controlUsuario.abrirActivity(PantallaAjustes.this, PantallaInicio.class);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaAjustes.this, PantallaPerfil.class);
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaAjustes.this, PantallaExplorar.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PantallaAjustes.this, "Has cerrarado sesión", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(PantallaAjustes.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {
        controlDialogos.dialogoSalir(PantallaAjustes.this);
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


}