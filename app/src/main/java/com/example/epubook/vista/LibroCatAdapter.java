package com.example.epubook.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

public class LibroCatAdapter extends RecyclerView.Adapter<LibroCatAdapter.ViewHolder> implements Filterable {

    List<LibroExplorar> listaLibros = new ArrayList<>();
    List<LibroExplorar> librosFiltro = new ArrayList<>();
    ControlExplorar controlExplorar;

    ExpCabeceraAdapter expCabeceraAdapter;


    public LibroCatAdapter(List<LibroExplorar> listaLibros){
        this.listaLibros = listaLibros;
        this.librosFiltro = listaLibros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_llibroscat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibroExplorar libro = librosFiltro.get(position);
        holder.titulo.setText(libro.getTitulo());
        holder.autor.setText(libro.getAutor());
        holder.portada.setImageBitmap(libro.getPortada());

        controlExplorar = new ControlExplorar(holder.itemView.getContext());
        expCabeceraAdapter = new ExpCabeceraAdapter(listaLibros);

        comprobarDescargado(holder, libro);

        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_librocat);
        holder.itemView.startAnimation(anim);

        holder.descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.descargando.setVisibility(View.VISIBLE);
                holder.descargar.setVisibility(View.INVISIBLE);

                controlExplorar.descargarLibroCat(libro.getRuta(), libro, LibroCatAdapter.this);
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
                        holder.descargar.setVisibility(View.VISIBLE);
                        holder.descargar.setText("Descargado");
                        holder.descargar.setEnabled(false);
                        break;
                    }else{
                        holder.descargar.setVisibility(View.VISIBLE);
                        holder.descargar.setText("Descargar");
                        holder.descargar.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return librosFiltro.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<LibroExplorar> libros = new ArrayList<>();

                if(charSequence == null || charSequence.length() == 0){
                    libros.addAll(listaLibros);
                }else{
                    String titulo = charSequence.toString().toLowerCase().trim();
                    String noEspacios = titulo.replaceAll(" ", "");

                    for(LibroExplorar libro : listaLibros){
                        String resultado = libro.getTitulo().toLowerCase().replaceAll("[\\s-]+", "");
                        if(resultado.contains(noEspacios)){
                            libros.add(libro);
                        }
                    }

                    librosFiltro = libros;
                }

                results.values = libros;
                results.count = libros.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                librosFiltro = (List<LibroExplorar>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView portada;
        private Button descargar;
        private ProgressBar descargando;
        private TextView titulo, autor, resumen, volver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            portada = itemView.findViewById(R.id.portadaCat);
            descargar = itemView.findViewById(R.id.descLibroCat);
            titulo = itemView.findViewById(R.id.tituloLibCat);
            autor = itemView.findViewById(R.id.autorLibCat);
            resumen = itemView.findViewById(R.id.resumenCat);
            volver = itemView.findViewById(R.id.volverItem);
            descargando = itemView.findViewById(R.id.libroCatCarg);
        }
    }

}
