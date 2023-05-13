package com.example.epubook.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlColecciones;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.modelo.Coleccion;
import com.example.epubook.vista.ColeccAdapter;

import java.util.ArrayList;
import java.util.List;

public class ColeccionesFragment extends Fragment {

    List<Coleccion> listaColeccion = new ArrayList<>();

    private RecyclerView recyclerView;
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
        noColecc = view.findViewById(R.id.noColecc);

        coleccAdapter = new ColeccAdapter(listaColeccion);
        recyclerView.setAdapter(coleccAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        controlColecciones.mostrarColecciones(listaColeccion, coleccAdapter, noColecc);


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

