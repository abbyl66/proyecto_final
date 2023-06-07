package com.example.epubook.vista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.controlador.ControlUsuario;
import com.example.epubook.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PantallaPerfil extends AppCompatActivity {

    //Mismas variables que se usan para el fragment desplegable.
    private DrawerLayout drawerLayout;
    private ImageView menu, imagenPerfil;
    private LinearLayout inicio, perfil, ajustes, cerrarSesion, explorar;

    //Variables que usaré para mostrar la información del usuario.
    private TextView nombreTitulo, numLibros, numColecc, noacciones;
    private ImageButton cambiarFoto;
    private Button cambiarNombre;

    private RecyclerView recyclerHistorial;
    private HistorialAdapter historialAdapter;

    public static final int imagenCod = 1;

    ControlUsuario controlUsuario = new ControlUsuario(PantallaPerfil.this);
    ControlDialogos controlDialogos = new ControlDialogos(PantallaPerfil.this);

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
        explorar = findViewById(R.id.explorar);

        nombreTitulo = findViewById(R.id.nombreTitP);
        cambiarFoto = findViewById(R.id.cambimgP);
        imagenPerfil = findViewById(R.id.imgPerfil);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        noacciones = findViewById(R.id.noacciones);

        cambiarNombre = findViewById(R.id.cambiarNombre);

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

        cambiarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaPerfil.this, CambiarNombre.class);
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

            guardarFotoPerfil(imagenUri);
            Toast.makeText(this, "Foto de perfil cambiado.", Toast.LENGTH_SHORT).show();
            imagenPerfil.setImageURI(imagenUri);

        }
    }

    private void guardarFotoPerfil(Uri uri) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("AAAUsuarios").child(uid);

        File file = new File(uri.getPath());

        storageReference.child(file.getName()).putFile(uri);
        reference.child("fotoPerfil").setValue(file.getName());

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

        //Obtengo el usuario actual.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){

            //Obtengo su id.
            String uid = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users");
            DatabaseReference databaseReference = reference.child(uid);

            //Accedo a la base de datos de este usuario a través del id.
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    //Extraigo los siguientes datos necesarios que mostraré.
                    String nombre = usuario.getNombre();
                    List<String> historialUser = usuario.getHistorial();
                    String foto = usuario.getFotoPerfil();

                    //Hago la referencia hasta el nodo donde se encuentra la foto actual de perfil del usuario.
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("AAAUsuarios").child(uid).child(foto);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Una vez obtengo la url de descarga de la imagen, lo muestro en el imageview de foto de perfil.
                            //Me aseguro de que la pantalla no esté inactiva al momento de cargar la image, esto lleva a muchos errores.
                            if(PantallaPerfil.this.isDestroyed()){
                                System.out.println("No carga imagen.");
                                return;
                            }else{
                                Glide.with(PantallaPerfil.this).load(uri).into(imagenPerfil);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                    });

                    //Muestro el nombre del usuario.
                    nombreTitulo.setText(nombre);

                    //Muestro el contenido del historial si lo hay.
                    if(historialUser != null){
                        Collections.reverse(historialUser);

                        historialAdapter = new HistorialAdapter(historialUser);
                        recyclerHistorial.setAdapter(historialAdapter);
                        recyclerHistorial.setLayoutManager(new LinearLayoutManager(PantallaPerfil.this));
                    }else{
                        noacciones.setVisibility(View.VISIBLE);
                        recyclerHistorial.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        controlDialogos.dialogoSalir(PantallaPerfil.this);

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