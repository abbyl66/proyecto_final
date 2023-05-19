package com.example.epubook.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.modelo.Libro;

import java.util.List;

public class LibroColeccAdapter extends RecyclerView.Adapter<LibroColeccAdapter.ViewHolder> {

    private List<Libro> listaLibros;

    public LibroColeccAdapter(List<Libro> listaLibros){
        this.listaLibros = listaLibros;
    }

    @Override
    public LibroColeccAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_libro_coleccion, parent, false);
        return new LibroColeccAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LibroColeccAdapter.ViewHolder holder, int position) {
        Libro libro = listaLibros.get(position);
        holder.titulo.setText(libro.getTitulo());
        holder.autor.setText(libro.getAutor());
        holder.portada.setImageBitmap(libro.getPortada());
    }

    @Override
    public int getItemCount() {
        return listaLibros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titulo, autor;
        private ImageView portada;
        public ViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.tituloLibroColecc);
            autor = itemView.findViewById(R.id.autorLibroColecc);
            portada = itemView.findViewById(R.id.portadaLibroColecc);

        }
    }

}