package com.example.epubook.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlExplorar;
import com.example.epubook.modelo.Libro;
import com.example.epubook.modelo.LibroExplorar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class ExpCabeceraAdapter extends RecyclerView.Adapter<ExpCabeceraAdapter.ViewHolder> {

    private List<LibroExplorar> listaLibros;
    ControlExplorar controlExplorar;

    public ExpCabeceraAdapter(List<LibroExplorar> listaLibros){
        this.listaLibros = listaLibros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_libro_expl, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibroExplorar libro = listaLibros.get(position);
        holder.portada.setImageBitmap(libro.getPortada());

        controlExplorar = new ControlExplorar(holder.itemView.getContext());

        comprobarDescargado(holder, libro);

        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_portadas);
        holder.itemView.startAnimation(anim);

        holder.descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.descargando.setVisibility(View.VISIBLE);
                holder.descargar.setVisibility(View.INVISIBLE);
                controlExplorar.descargarLibroCab(libro.getRuta(), libro, ExpCabeceraAdapter.this);
            }
        });

    }

    private void comprobarDescargado(ViewHolder holder, LibroExplorar libro) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario, donde guardaré los ficheros epub de cada usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis libros y epub.
        StorageReference referenceLibros = referenceUsuario.child("misLibros");

        referenceLibros.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                holder.descargando.setVisibility(View.INVISIBLE);
                for(StorageReference epub : listResult.getItems()){
                    //Extraigo el nombre de la ruta.
                    String archivo = epub.getName();
                    int index = archivo.lastIndexOf('.');
                    String nombreArch = archivo.substring(0, index);

                    File archivoLibro = new File(libro.getRuta());
                    String archivo2 = archivoLibro.getName();
                    int index2 = archivo2.lastIndexOf('.');
                    String nombreArch2 = archivo2.substring(0, index2);

                    //Los igualo para saber si ya existe.
                    if(nombreArch.equals(nombreArch2)){
                        holder.descargado.setVisibility(View.VISIBLE);
                        holder.descargar.setVisibility(View.INVISIBLE);
                        break;
                    }else{
                        holder.descargado.setVisibility(View.INVISIBLE);
                        holder.descargar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    //Limito el numero de libros que saldrán.
    @Override
    public int getItemCount() {
        int limite = 6;
        return Math.min(listaLibros.size(), limite);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView portada;
        private ImageButton descargar;
        private ProgressBar descargando;
        private ImageView descargado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            portada = itemView.findViewById(R.id.portadaExpl);
            descargar= itemView.findViewById(R.id.portbtDesc);
            descargando = itemView.findViewById(R.id.libroCabCarg);
            descargado = itemView.findViewById(R.id.libroCabDesc);
        }
    }

}
