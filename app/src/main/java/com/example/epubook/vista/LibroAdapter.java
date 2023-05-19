package com.example.epubook.vista;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.modelo.Libro;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.ViewHolder> implements Filterable {

    private List<Libro> listaLibros;
    private List<Libro> librosFiltro;
    private OnItemClickListener listenerClick;

    public LibroAdapter(List<Libro> listaLibros){
        this.listaLibros = listaLibros;
        this.librosFiltro = listaLibros;
    }

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        listenerClick = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_libro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Libro libro = librosFiltro.get(position);
        holder.titulo.setText(libro.getTitulo());
        holder.autor.setText(libro.getAutor());
        holder.portada.setImageBitmap(libro.getPortada());

        Animation animItems = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_items);
        holder.itemView.startAnimation(animItems);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenerClick != null){
                    listenerClick.onItemClick(position);
                }
            }
        });

        holder.guardarColecc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControlDialogos controlDialogos = new ControlDialogos(view.getContext());
                controlDialogos.dialogoColeccion(view, position, libro.getRuta());
            }
        });

        comprobarLibrosGuardados(holder, libro);

    }

    private void comprobarLibrosGuardados(ViewHolder holder, Libro libro) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference referenceUsuario = reference.child(uidUsuario);

        StorageReference referenceColecc = referenceUsuario.child("misColecciones/");

        referenceColecc.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference storageReference : listResult.getPrefixes()){
                    storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for(StorageReference libros : listResult.getItems()){
                                if(!libros.getName().equals("ficheroVacio")){
                                    File libroEpub = new File(libro.getRuta());
                                    if (libroEpub.getName().equals(libros.getName())){
                                        holder.guardarColecc.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.celeste));
                                        break;
                                    }

                                }
                            }
                        }
                    });
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
                List<Libro> libros = new ArrayList<>();

                if(charSequence == null || charSequence.length() == 0){
                    libros.addAll(listaLibros);
                }else{
                    String titulo = charSequence.toString().toLowerCase().trim();
                    String noEspacios = titulo.replaceAll(" ", "");

                    for(Libro libro : listaLibros){
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
                librosFiltro = (List<Libro>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titulo, autor;
        private ImageView portada, guardarColecc;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.libroTitulo);
            autor = itemView.findViewById(R.id.libroAutor);
            portada = itemView.findViewById(R.id.libroPortada);
            guardarColecc = itemView.findViewById(R.id.botonGuardarColecc);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
