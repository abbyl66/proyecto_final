package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PantallaInicio extends AppCompatActivity{

    private FloatingActionButton botonAniadir;
    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion, explorar;
    public static BottomNavigationView bottomNavigationView;

    private ControlUsuario controlUsuario = new ControlUsuario(PantallaInicio.this);
    private ControlDialogos controlDialogos = new ControlDialogos(PantallaInicio.this);

    private boolean unClick = true;
    final  int dobleClickTiempo = 700;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
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
        explorar = findViewById(R.id.explorar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference =  FirebaseStorage.getInstance().getReference().child(user.getUid());
        StorageReference usuariosFoto =  FirebaseStorage.getInstance().getReference().child("AAAUsuarios").child(user.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        //En caso de que no se lleva a cabo el proceso de registro con el email verificado, se elimina el usuario.
        if(user != null){
            if(!user.isEmailVerified()){
                FirebaseAuth.getInstance().signOut();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Tambien elimino los campos que se crean de forma automática para cada usuario para almacenar su información.
                        storageReference.delete();
                        databaseReference.removeValue();
                        usuariosFoto.delete();

                        Log.d(task.toString(), "Usuario eliminado");
                        Intent intent = new Intent(PantallaInicio.this, PlantillaDeslizar.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }


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
                Intent intent = new Intent(PantallaInicio.this, PantallaInicio.class);
                startActivity(intent);
            }
        });

        //Abro perfil con la información del usuario que ha iniciado sesión.
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                if(user != null){
                    controlUsuario.abrirActivity(PantallaInicio.this, PantallaPerfil.class);
                }

            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaInicio.this, PantallaAjustes.class);
            }
        });

        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaInicio.this, PantallaExplorar.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PantallaInicio.this, "Has cerrarado sesión", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(PantallaInicio.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

        replaceFragment(new LibrosFragment());

        //Control al pulsar en el menú inferior.
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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


                switch (item.getItemId()){
                    case R.id.mab_libros:
                        replaceFragment(new LibrosFragment());
                        break;
                    case R.id.mab_coleccion:
                        replaceFragment(new ColeccionesFragment());
                        break;
                }
            }
            return true;
        });

        //Botón añadir del menú inferior.
        botonAniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unClick) {

                    unClick = false;

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            unClick = true;
                        }
                    };

                    //Tiempo que no funcionará el click.
                    handler.postDelayed(runnable, dobleClickTiempo);

                    controlDialogos.mostrarDialogoAniadir(PantallaInicio.this, bottomNavigationView);
                }
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

}