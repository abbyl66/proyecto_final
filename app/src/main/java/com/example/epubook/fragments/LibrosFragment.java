package com.example.epubook.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlEpub;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.LibroAdapter;

import java.util.ArrayList;
import java.util.List;

public class LibrosFragment extends Fragment {

    List<Libro> listalibros = new ArrayList<>();
    private RecyclerView recyclerView;
    private LibroAdapter libroAdapter;
    ControlEpub controlEpub;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controlEpub = new ControlEpub(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_libros, container, false);

        recyclerView = view.findViewById(R.id.libroRecyclerView);

        libroAdapter = new LibroAdapter(listalibros);
        recyclerView.setAdapter(libroAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        controlEpub.obtenerMisLibros(libroAdapter, listalibros);

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