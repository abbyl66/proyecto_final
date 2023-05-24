package com.example.epubook.vista;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.modelo.ArchivoEpub;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

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

    //Cada archivo epub tendrá el diseño de layout_archivoepub.
    @Override
    public EpubAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_archivoepub, parent, false);
        return new EpubAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpubAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //Uso archivosFiltro porque este va a determinar los archivos que se mostrarán en mi recyclerview.
        ArchivoEpub archivoEpub = archivosFiltro.get(position);

        //Obtengo los datos de los archivos.
        holder.nombre.setText(archivoEpub.getNombre());

        //Cambio el formato del tamanio para que se vea mejor.
        String tamanio = formatoTamanio(archivoEpub.getTamanio());
        holder.tamanio.setText(tamanio);

        //Doy formato a la fecha.
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = s.format(archivoEpub.getFecha());
        holder.fecha.setText(fecha);

        comprobarGuardado(holder, archivoEpub);

        Animation animacion = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_itemsepub);
        holder.itemView.startAnimation(animacion);

        //Esto lo usaré cuando el usuario seleccione un archivo. Que posteriormente, leeré.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenerClick != null){
                    listenerClick.onItemClick(position);
                    holder.guardandoEpub.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //Compruebo qué libros están guardados en la biblioteca para mostralos con un icon check.
    private void comprobarGuardado(ViewHolder holder, ArchivoEpub archivoEpub) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Nodo usuario, donde guardaré los ficheros epub de cada usuario.
        StorageReference referenceUsuario = reference.child(uidUsuario);

        //Nodo mis libros y epub.
        StorageReference referenceLibros = referenceUsuario.child("misLibros");

        referenceLibros.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                holder.guardandoEpub.setVisibility(View.INVISIBLE);
                for(StorageReference epub : listResult.getItems()){
                    //Extraigo el nombre de la ruta.
                    String archivo = epub.getName();
                    int index = archivo.lastIndexOf('.');
                    String nombreArch = archivo.substring(0, index);

                    String archivo2 = archivoEpub.getNombre();
                    int index2 = archivo2.lastIndexOf('.');
                    String nombreArch2 = archivo2.substring(0, index2);

                    //Los igualo para saber si ya existe.
                    if(nombreArch.equals(nombreArch2)){
                        holder.epubGuardado.setVisibility(View.VISIBLE);
                        break;
                    }else{
                        holder.epubGuardado.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    //Método para dar formato al tamanio.
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

    //Filtro de archivos.
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<ArchivoEpub> archivosEpub = new ArrayList<>();

                //En caso de que no se haya escrito nada, devuelve todos los archivos encontrados.
                if(charSequence == null ||charSequence.length() == 0){
                    archivosEpub.addAll(listaarchivos);

                }else{
                    //Al haber algo escrito, ignoro los espacios.
                    String f = charSequence.toString().toLowerCase().trim();
                    String filtro = f.replaceAll(" ", "");
                    //Recorro los archivos.
                    for(ArchivoEpub a : listaarchivos){
                        //Ignoro guiones y espacios de los archivos encontrados.
                        String nombre = a.getNombre().toLowerCase().replaceAll("[\\s-]+", "");
                        //Compruebo que nombre coincida con filtro, que es lo que se ha introducido por teclado.
                        if(nombre.contains(filtro)){
                            //Añado el libro coincidente a la lista.
                            archivosEpub.add(a);
                        }
                    }
                    //Guardo la lista.
                    archivosFiltro = archivosEpub;
                }

                //Muestro la lista. Envío los datos y el número de archivos encontrados.
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

    //Establezco la relación entre variables.
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nombre, tamanio, fecha;
        private ImageView epubGuardado;
        private ProgressBar guardandoEpub;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreArch);
            tamanio = itemView.findViewById(R.id.tamanioArch);
            fecha = itemView.findViewById(R.id.fechaArch);
            epubGuardado = itemView.findViewById(R.id.libroGuardado);
            guardandoEpub = itemView.findViewById(R.id.cargaArchivoEpub);

            itemView.setOnClickListener(this);

        }

        public void onClick(View view){

        }
    }

}
