package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.google.firebase.auth.FirebaseAuth;

public class PantallaPerfil extends AppCompatActivity {

    //Mismas variables que se usan para el fragment desplegable.
    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion;

    //Variables que usaré para mostrar la información del usuario.
    TextView nombrePerfil, emailPerfil, userPerfil, contrPerfil;
    TextView nombreTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_perfil);

        drawerLayout = findViewById(R.id.dsp_contenido);
        menu = findViewById(R.id.menu);
        inicio = findViewById(R.id.inicio);
        perfil = findViewById(R.id.perfil);
        ajustes = findViewById(R.id.ajustes);
        cerrarSesion = findViewById(R.id.cerrarSesion);

        nombrePerfil = findViewById(R.id.nombrePerfil);
        emailPerfil = findViewById(R.id.emailPerfil);
        userPerfil = findViewById(R.id.userPerfil);
        contrPerfil = findViewById(R.id.contrPerfil);
        nombreTitulo = findViewById(R.id.nombreTitP);

        infoUsuario();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActivity(PantallaPerfil.this, PantallaInicio.class);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActivity(PantallaPerfil.this, PantallaAjustes.class);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PantallaPerfil.this, "Has cerrarado sesión", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(PantallaPerfil.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //Obtengo informacón pasada desde inicio de sesión.
    public void infoUsuario(){
        Intent intent = getIntent();

        String nombreUser = intent.getStringExtra("nombre");
        String emailUser = intent.getStringExtra("email");
        String userUser = intent.getStringExtra("user");
        String contrUser = intent.getStringExtra("ctrsenia");

        nombreTitulo.setText(nombreUser);
        nombrePerfil.setText(nombreUser);
        emailPerfil.setText(emailUser);
        userPerfil.setText(userUser);
        contrPerfil.setText(contrUser);
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void abrirActivity(Activity activity, Class activity2){
        Intent intent = new Intent(activity, activity2);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intentP = getIntent();

        String nombreUser = intentP.getStringExtra("nombre");
        String emailUser = intentP.getStringExtra("email");
        String userUser = intentP.getStringExtra("user");
        String contrUser = intentP.getStringExtra("ctrsenia");

        intent.putExtra("nombre", nombreUser);
        intent.putExtra("email", emailUser);
        intent.putExtra("user", userUser);
        intent.putExtra("ctrsenia", contrUser);


        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}