package com.example.epubook.vista;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Coleccion;

import java.util.List;

public class ColeccAdapter extends RecyclerView.Adapter<ColeccAdapter.ViewHolder> {

    private List<Coleccion> listaColecciones;
    private OnColeccClick coleccListener;

    public ColeccAdapter(List<Coleccion> listaColecciones){
        this.listaColecciones = listaColecciones;
    }

    public interface OnColeccClick{
        void onColeccClick(int pos);
    }

    public void setOnColeccListener(OnColeccClick listener){
        coleccListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_colecc, parent, false);
        return new ColeccAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Coleccion coleccion = listaColecciones.get(position);

        holder.nombreColecc.setText(coleccion.getNombre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coleccListener!=null){
                    holder.relativeLayout.setBackgroundColor(Color.WHITE);
                    holder.nombreColecc.setTextColor(Color.parseColor("#f9d2d7"));
                    coleccListener.onColeccClick(position);
                }
            }
        });
        

        Animation animacion = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_itemsepub);
        holder.itemView.startAnimation(animacion);

    }

    @Override
    public int getItemCount() {
        return listaColecciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nombreColecc;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            nombreColecc = itemView.findViewById(R.id.coleccNombre);
            relativeLayout = itemView.findViewById(R.id.fondoColeccColor);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }

}
