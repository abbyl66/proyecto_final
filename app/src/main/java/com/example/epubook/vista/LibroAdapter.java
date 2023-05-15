package com.example.epubook.vista;

import android.annotation.SuppressLint;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.fragments.LibrosFragment;
import com.example.epubook.modelo.Libro;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        private ImageView portada;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.libroTitulo);
            autor = itemView.findViewById(R.id.libroAutor);
            portada = itemView.findViewById(R.id.libroPortada);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
