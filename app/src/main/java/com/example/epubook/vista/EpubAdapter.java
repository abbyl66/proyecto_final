package com.example.epubook.vista;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.ArchivoEpub;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EpubAdapter extends RecyclerView.Adapter<EpubAdapter.ViewHolder> implements Filterable {

    private List<ArchivoEpub> listaarchivos;
    private List<ArchivoEpub> archivosFiltro;
    private OnItemClickListener listenerClick;

    public EpubAdapter(List<ArchivoEpub> listaarchivos){
        this.listaarchivos = listaarchivos;
        this.archivosFiltro = listaarchivos;
    }

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        listenerClick = listener;
    }

    @Override
    public EpubAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_archivoepub, parent, false);
        return new EpubAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpubAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ArchivoEpub archivoEpub = archivosFiltro.get(position);
        holder.nombre.setText(archivoEpub.getNombre());

        String tamanio = formatoTamanio(archivoEpub.getTamanio());
        holder.tamanio.setText(tamanio);

        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = s.format(archivoEpub.getFecha());
        holder.fecha.setText(fecha);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenerClick != null){
                    listenerClick.onItemClick(position);
                }
            }
        });

    }

    public String formatoTamanio(Double tamanio){
        String tam = "";

        //Tamanio menor a 1KB.
        if(tamanio < 1024){
            tam = tamanio+"B";

        //Menor a 1MB.
        }else if(tamanio < 1024 * 1024){
            tam = String.format("%.2f", tamanio/1024)+"KB";
        //Menos a 1GB.
        }else if (tamanio<1024*1024*1024){
            tam = String.format("%.2f", tamanio/(1024*1024))+"MB";
        //Mayor a 1GB.
        }else{
            tam = String.format("%.2f", tamanio/(1024*1024*1024))+"MB";
        }

        return tam;
    }

    @Override
    public int getItemCount() {
        return archivosFiltro.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<ArchivoEpub> archivosEpub = new ArrayList<>();

                if(charSequence == null ||charSequence.length() == 0){
                    archivosEpub.addAll(listaarchivos);

                }else{
                    String f = charSequence.toString().toLowerCase().trim();
                    String filtro = f.replaceAll(" ", "");
                    for(ArchivoEpub a : listaarchivos){
                        String nombre = a.getNombre().toLowerCase().replaceAll("[\\s-]+", "");
                        if(nombre.contains(filtro)){
                            archivosEpub.add(a);
                        }
                    }

                    archivosFiltro = archivosEpub;
                }

                results.values = archivosEpub;
                results.count= archivosEpub.size();
                return results;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                archivosFiltro = (List<ArchivoEpub>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nombre, tamanio, fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreArch);
            tamanio = itemView.findViewById(R.id.tamanioArch);
            fecha = itemView.findViewById(R.id.fechaArch);

            itemView.setOnClickListener(this);

        }

        public void onClick(View view){

        }
    }

}
