package com.example.epubook.vista;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Libro;

import java.util.List;

public class LibroColeccAdapter extends RecyclerView.Adapter<LibroColeccAdapter.ViewHolder> {

    private List<Libro> listaLibros;

    private ItemLongClick itemLongClick;

    private ItemClick itemClick;
    public LibroColeccAdapter(List<Libro> listaLibros){
        this.listaLibros = listaLibros;
    }

    @Override
    public LibroColeccAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_libro_coleccion, parent, false);
        return new LibroColeccAdapter.ViewHolder(view, this);
    }

    public interface ItemLongClick{
        void onItemLongClick(View view, int pos);
    }

    public interface ItemClick{
        void onItemClick(int pos);
    }

    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }

    public void setItemLongClick(ItemLongClick itemLongClick){
        this.itemLongClick = itemLongClick;
    }

    @Override
    public void onBindViewHolder(LibroColeccAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Libro libro = listaLibros.get(position);
        holder.titulo.setText(libro.getTitulo());
        holder.autor.setText(libro.getAutor());
        holder.portada.setImageBitmap(libro.getPortada());


        //Animacion de libros dentro de colecciones.
        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.items_libros_colecc);
        holder.itemView.startAnimation(anim);

    }

    @Override
    public int getItemCount() {
        return listaLibros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titulo, autor;
        private ImageView portada;

        private LibroColeccAdapter libroColeccAdapter;

        public ViewHolder(View itemView, LibroColeccAdapter libroColeccAdapter) {
            super(itemView);
            this.libroColeccAdapter = libroColeccAdapter;
            titulo = itemView.findViewById(R.id.tituloLibroColecc);
            autor = itemView.findViewById(R.id.autorLibroColecc);
            portada = itemView.findViewById(R.id.portadaLibroColecc);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(libroColeccAdapter.itemLongClick!=null){
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            libroColeccAdapter.itemLongClick.onItemLongClick(itemView, pos);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            if(libroColeccAdapter.itemClick != null){
                int pos = getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    libroColeccAdapter.itemClick.onItemClick(pos);
                }

            }
        }
    }

}