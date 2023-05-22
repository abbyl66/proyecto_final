package com.example.epubook.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlColecciones;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.ColeccAdapter;
import com.example.epubook.vista.LibroColeccAdapter;
import com.example.epubook.vista.SuperposItems;

import java.util.ArrayList;
import java.util.List;

public class ColeccionesFragment extends Fragment {

    List<Coleccion> listaColeccion = new ArrayList<>();
    List<Libro> listaLibros = new ArrayList<>();
    LibroColeccAdapter libroColeccAdapter;

    private RecyclerView recyclerView, recyclerCotenido;
    private ColeccAdapter coleccAdapter;

    ControlColecciones controlColecciones;
    ControlDialogos controlDialogos;

    private TextView noColecc, titulo, titulo2;
    private ImageView imgColecc, contenedor;

    LinearLayout toolbarInicio;
    LinearLayout toolbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controlColecciones = new ControlColecciones(context);
        controlDialogos = new ControlDialogos(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colecciones, container, false);

        toolbar  = requireActivity().findViewById(R.id.toolbarEliminar);
        toolbarInicio  = requireActivity().findViewById(R.id.toolbarInicio);

        toolbar.setVisibility(View.GONE);
        toolbarInicio.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerViewColecc);
        recyclerCotenido = view.findViewById(R.id.recyclerContenidoColecc);

        noColecc = view.findViewById(R.id.noColecc);
        noColecc.setVisibility(View.GONE);

        titulo = view.findViewById(R.id.tituloColecc);
        titulo2 = view.findViewById(R.id.tituloColecc2);
        imgColecc = view.findViewById(R.id.imgColecc);
        contenedor = view.findViewById(R.id.contenedorColecc);

        coleccAdapter = new ColeccAdapter(listaColeccion);
        recyclerView.setAdapter(coleccAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        int tamanio = 100;
        SuperposItems superposItems = new SuperposItems(tamanio);
        recyclerView.addItemDecoration(superposItems);

        libroColeccAdapter = new LibroColeccAdapter(listaLibros);
        recyclerCotenido.setAdapter(libroColeccAdapter);
        recyclerCotenido.setLayoutManager(new LinearLayoutManager(getActivity()));

        controlColecciones.mostrarColecciones(listaColeccion, coleccAdapter);

        coleccAdapter.setOnColeccListener(new ColeccAdapter.OnColeccClick() {
            @Override
            public void onColeccClick(int pos) {
                //Al pulsa alguna coleccion, oculto titulos e imagen de portada.Y control del toolbar eliminar.
                titulo.setVisibility(View.GONE);
                titulo2.setVisibility(View.GONE);
                imgColecc.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                toolbarInicio.setVisibility(View.VISIBLE);

                //Uso un contendor blanco para que el aspecto del fragment colecciones se vea bien.
                if(pos==0){
                    contenedor.setVisibility(View.VISIBLE);
                }else{
                    contenedor.setVisibility(View.INVISIBLE);
                }

                controlColecciones.mostrarContenidoColecc(listaLibros, libroColeccAdapter, listaColeccion.get(pos), getActivity());

                libroColeccAdapter.setItemLongClick(new LibroColeccAdapter.ItemLongClick() {
                    @Override
                    public void onItemLongClick(View view, int posLibro) {
                        toolbarEliminarItem(posLibro, listaColeccion.get(pos), view);
                    }
                });

            }
        });



        if(getActivity() != null){
            getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Toast.makeText(getActivity(), "Dialogo salir", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;

    }

    private void toolbarEliminarItem(int posLibro, Coleccion coleccion, View view) {
        toolbarInicio.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);

        ImageButton eliminar = requireActivity().findViewById(R.id.eliminarLibColecc);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDialogos.dialogoEliminarLibroColecc(view, posLibro, listaLibros, libroColeccAdapter, coleccion, ColeccionesFragment.this);
                toolbar.setVisibility(View.GONE);
                toolbarInicio.setVisibility(View.VISIBLE);
            }
        });

        //Oculto toolbar en caso de que no se quiera seguir con el proceso de eliminar libro.
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toolbar.getVisibility()==View.VISIBLE){
                    toolbar.setVisibility(View.GONE);
                }
            }
        });

    }
}

