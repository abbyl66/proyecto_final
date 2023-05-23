package com.example.epubook.vista;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;

import org.w3c.dom.Text;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private List<String> lisaCategorias;

    public CategoriaAdapter(List<String> lisaCategorias){
        this.lisaCategorias = lisaCategorias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String categoria = lisaCategorias.get(position);
        holder.nombre.setText(categoria);
        holder.puntoGuia.setVisibility(View.INVISIBLE);

        holder.nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.nombre.setTextColor(Color.parseColor("#FFf9d2d7"));
                holder.puntoGuia.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lisaCategorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private ImageView puntoGuia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreCateg);
            puntoGuia = itemView.findViewById(R.id.puntoGuiaExp);
        }
    }
}
