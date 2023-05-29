package com.example.epubook.vista;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlUsuario;
import com.example.epubook.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class PantallaPerfil extends AppCompatActivity {

    //Mismas variables que se usan para el fragment desplegable.
    private DrawerLayout drawerLayout;
    private ImageView menu, imagenPerfil;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion, escribir, explorar;

    //Variables que usaré para mostrar la información del usuario.
    private TextView nombreTitulo, numLibros, numColecc;
    private ImageButton cambiarFoto;

    public static final int imagenCod = 1;

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

        nombreTitulo = findViewById(R.id.nombreTitP);
        cambiarFoto = findViewById(R.id.cambimgP);
        imagenPerfil = findViewById(R.id.imgPerfil);

        numLibros = findViewById(R.id.librosPerfil);
        numColecc = findViewById(R.id.coleccPerfil);

        numLibrosColecc();

        infoUsuario();

        cambiarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, imagenCod);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == imagenCod && resultCode == RESULT_OK && data != null){
            Uri imagenUri = data.getData();

            imagenPerfil.setImageURI(imagenUri);
            guardarFotoPerfil(imagenUri);
            Toast.makeText(this, "Foto de perfil cambiado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarFotoPerfil(Uri uri) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

        reference.child("fotoPerfil").setValue(uri.toString());

    }

    private void numLibrosColecc() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        //Referencia del archivo que elimaré.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference referenceLibro = storage.getReference().child(uid).child("misLibros");
        StorageReference referenceColecc = storage.getReference().child(uid).child("misColecciones");

        referenceLibro.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                int num = listResult.getItems().size();
                numLibros.setText(String.valueOf(num));
            }
        });

        referenceColecc.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                int num = listResult.getPrefixes().size();
                numColecc.setText(String.valueOf(num));
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
                    String foto = usuario.getFotoPerfil();

                    nombreTitulo.setText(nombre);
                    imagenPerfil.setImageURI(Uri.parse(foto));
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