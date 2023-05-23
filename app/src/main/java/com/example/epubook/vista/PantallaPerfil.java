package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlUsuario;
import com.example.epubook.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PantallaPerfil extends AppCompatActivity {

    //Mismas variables que se usan para el fragment desplegable.
    private DrawerLayout drawerLayout;
    private ImageView menu;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion, escribir, explorar;

    //Variables que usaré para mostrar la información del usuario.
    private TextView nombrePerfil, emailPerfil, userPerfil, contrPerfil;
    private TextView nombreTitulo;

    private Button cambiarNombre, cambiarUsuario, cambiarEmail;

    private FirebaseAuth auth;

    ControlUsuario controlUsuario = new ControlUsuario(PantallaPerfil.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.temaRosa);
        setContentView(R.layout.activity_pantalla_perfil);

        drawerLayout = findViewById(R.id.dsp_contenido);
        menu = findViewById(R.id.menu);
        inicio = findViewById(R.id.inicio);
        perfil = findViewById(R.id.perfil);
        ajustes = findViewById(R.id.ajustes);
        cerrarSesion = findViewById(R.id.cerrarSesion);
        escribir = findViewById(R.id.escribir);
        explorar = findViewById(R.id.explorar);

        nombrePerfil = findViewById(R.id.nombrePerfil);
        emailPerfil = findViewById(R.id.emailPerfil);
        userPerfil = findViewById(R.id.userPerfil);
        contrPerfil = findViewById(R.id.contrPerfil);
        nombreTitulo = findViewById(R.id.nombreTitP);


        cambiarNombre = findViewById(R.id.editarNombre);
        cambiarUsuario = findViewById(R.id.editarUsuario);
        cambiarEmail = findViewById(R.id.editarEmail);

        auth = FirebaseAuth.getInstance();

        infoUsuario();


        cambiarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaPerfil.this, CambiarNombre.class);
                startActivity(intent);
            }
        });

        cambiarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaPerfil.this, CambiarUser.class);
                startActivity(intent);
            }
        });

        cambiarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaPerfil.this, CambiarEmail.class);
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
                controlUsuario.abrirActivity(PantallaPerfil.this, PantallaInicio.class);
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
                controlUsuario.abrirActivity(PantallaPerfil.this, PantallaAjustes.class);
            }
        });

        escribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaPerfil.this, PantallaEscribir.class);
            }
        });

        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlUsuario.abrirActivity(PantallaPerfil.this, PantallaExplorar.class);
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

    //Método para obtener información del usuario que mostraré en la pantalla perfil.
    public void infoUsuario(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){

            String uid = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users");
            DatabaseReference databaseReference = reference.child(uid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    String nombre = usuario.getNombre();
                    String user = usuario.getUser();
                    String email = usuario.getEmail();

                    nombreTitulo.setText("Información de " +nombre);
                    nombrePerfil.setText(nombre);
                    userPerfil.setText(user);
                    emailPerfil.setText(email);

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }

    }

    //Métodos para abrir y cerrar fragment izquierdo.
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