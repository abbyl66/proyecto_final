package com.example.epubook.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;

import org.w3c.dom.Text;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<String> registros;

    public HistorialAdapter(List<String> registros){
        this.registros = registros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_historial, parent, false);
        return new HistorialAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String registro = registros.get(position);
        holder.txtRegistro.setText(registro);

    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtRegistro, txtFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRegistro = itemView.findViewById(R.id.registroTxt);
        }
    }
}
