package com.example.epubook.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.Coleccion;

import java.util.List;

public class ColeccAdapter extends RecyclerView.Adapter<ColeccAdapter.ViewHolder> {

    private List<Coleccion> listaColecciones;

    public ColeccAdapter(List<Coleccion> listaColecciones){
        this.listaColecciones = listaColecciones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_colecc, parent, false);
        return new ColeccAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coleccion coleccion = listaColecciones.get(position);
        holder.nombreColecc.setText(coleccion.getNombre());
    }

    @Override
    public int getItemCount() {
        return listaColecciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nombreColecc;

        public ViewHolder(View itemView) {
            super(itemView);

            nombreColecc = itemView.findViewById(R.id.coleccNombre);

        }
    }

}
