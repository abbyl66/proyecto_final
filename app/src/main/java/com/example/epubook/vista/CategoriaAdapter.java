package com.example.epubook.vista;

import android.annotation.SuppressLint;
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

    private List<String> listaCategorias;
    private OnCatClick catListener;
    private int itemSelect = -1;

    public CategoriaAdapter(List<String> listaCategorias){
        this.listaCategorias = listaCategorias;
    }

    public interface OnCatClick{
        void onCatClick(int pos);
    }

    public void setOnCatListener(OnCatClick catClick){
        catListener = catClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categoria, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String categoria = listaCategorias.get(position);
        holder.nombre.setText(categoria);
        holder.puntoGuia.setVisibility(View.INVISIBLE);


        if (position == itemSelect) {

            holder.nombre.setTextColor(Color.parseColor("#FFf9d2d7"));
            holder.puntoGuia.setVisibility(View.VISIBLE);
            catListener.onCatClick(position);

        }else{
            holder.nombre.setTextColor(Color.parseColor("#FFAAACB1"));
            holder.puntoGuia.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nombre;
        private ImageView puntoGuia;

        private CategoriaAdapter categoriaAdapter;

        public ViewHolder(@NonNull View itemView, CategoriaAdapter categoriaAdapter) {
            super(itemView);
            this.categoriaAdapter = categoriaAdapter;
            nombre = itemView.findViewById(R.id.nombreCateg);
            puntoGuia = itemView.findViewById(R.id.puntoGuiaExp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            categoriaAdapter.itemSelect = pos;
            categoriaAdapter.notifyDataSetChanged();

            if(categoriaAdapter.catListener != null){
                categoriaAdapter.catListener.onCatClick(pos);
            }
        }
    }
}
