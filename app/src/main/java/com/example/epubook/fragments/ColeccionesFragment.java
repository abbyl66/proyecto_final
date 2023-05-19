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
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlColecciones;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.ColeccAdapter;
import com.example.epubook.vista.ColeccDialogAdapter;
import com.example.epubook.vista.LibroAdapter;
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

    private TextView noColecc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controlColecciones = new ControlColecciones(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colecciones, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewColecc);
        recyclerCotenido = view.findViewById(R.id.recyclerContenidoColecc);
        noColecc = view.findViewById(R.id.noColecc);

        coleccAdapter = new ColeccAdapter(listaColeccion);
        recyclerView.setAdapter(coleccAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        int tamanio = 100;
        SuperposItems superposItems = new SuperposItems(tamanio);
        recyclerView.addItemDecoration(superposItems);

        libroColeccAdapter = new LibroColeccAdapter(listaLibros);
        recyclerCotenido.setAdapter(libroColeccAdapter);
        recyclerCotenido.setLayoutManager(new LinearLayoutManager(getActivity()));

        controlColecciones.mostrarColecciones(listaColeccion, coleccAdapter, noColecc);

        coleccAdapter.setOnColeccListener(new ColeccAdapter.OnColeccClick() {
            @Override
            public void onColeccClick(int pos) {
                controlColecciones.mostrarContenidoColecc(listaLibros, libroColeccAdapter, listaColeccion.get(pos), getActivity());
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
}

