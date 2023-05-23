package com.example.epubook.vista;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Libro;

import java.util.List;

public class ExpCabeceraAdapter extends RecyclerView.Adapter<ExpCabeceraAdapter.ViewHolder> {

    private List<Libro> listaLibros;

    public ExpCabeceraAdapter(List<Libro> listaLibros){
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
        Libro libro = listaLibros.get(position);
        holder.portada.setImageBitmap(libro.getPortada());
        holder.descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(libro.getRuta());
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaLibros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView portada;
        private ImageButton descargar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            portada = itemView.findViewById(R.id.portadaExpl);
            descargar= itemView.findViewById(R.id.portbtDesc);
        }
    }

}
