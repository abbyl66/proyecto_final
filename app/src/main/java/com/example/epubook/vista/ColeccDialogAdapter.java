package com.example.epubook.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlColecciones;
import com.example.epubook.modelo.Coleccion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class ColeccDialogAdapter extends RecyclerView.Adapter<ColeccDialogAdapter.ViewHolder> {

private List<Coleccion> listaColecciones;
private String ruta;

public ColeccDialogAdapter(List<Coleccion> listaColecciones, String ruta){
        this.listaColecciones = listaColecciones;
        this.ruta = ruta;
        }

@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_colecc_recycler, parent, false);
        return new ColeccDialogAdapter.ViewHolder(view);
        }

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
    Coleccion coleccion = listaColecciones.get(position);
    holder.nombreColecc.setText(coleccion.getNombre());
    holder.elegirColecc.setOnCheckedChangeListener(null);
    holder.elegirColecc.setChecked(coleccion.isSeleccionado());
    holder.elegirColecc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            coleccion.setSeleccionado(b);
            if(b){
                coleccion.setSeleccionado(b);
            }else {
                coleccion.setSeleccionado(b);
            }
        }
    });

    comprobarGuardadoColecc(holder, coleccion);
}

    private void comprobarGuardadoColecc(ViewHolder holder, Coleccion coleccion) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();

        String uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference referenceUsuario = reference.child(uidUsuario);

        StorageReference referenceC = referenceUsuario.child("misColecciones");
        StorageReference referenceColeccion = referenceC.child(coleccion.getNombre());

        referenceColeccion.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference archivo : listResult.getItems()){
                    File libro = new File(ruta);
                    if(libro.getName().equals(archivo.getName())){
                        //Libro ya pertenece a una carpeta. No se puede quitar el check.
                        holder.elegirColecc.setChecked(true);
                        holder.elegirColecc.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
public int getItemCount() {
        return listaColecciones.size();
        }

public static class ViewHolder extends RecyclerView.ViewHolder{
    private TextView nombreColecc;
    private CheckBox elegirColecc;

    public ViewHolder(View itemView) {
        super(itemView);

        nombreColecc = itemView.findViewById(R.id.nombreColeccRecy);
        elegirColecc = itemView.findViewById(R.id.cbColeccion);

    }
}

}
