package com.example.epubook.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Libro;

import java.util.List;

public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.ViewHolder> {

    private List<Libro> listaLibros;

    public LibroAdapter(List<Libro> listaLibros){
        this.listaLibros = listaLibros;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_libro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Libro libro = listaLibros.get(position);
        holder.titulo.setText(libro.getTitulo());
        holder.autor.setText(libro.getAutor());
    }

    @Override
    public int getItemCount() {
        return listaLibros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView titulo, autor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.libroTitulo);
            autor = itemView.findViewById(R.id.libroAutor);

        }
    }
}
