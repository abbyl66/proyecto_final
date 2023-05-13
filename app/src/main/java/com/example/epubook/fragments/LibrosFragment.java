package com.example.epubook.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private ControlEpub controlEpub;
    private  LibroAdapter libroAdapter;

    private ProgressBar progressBar;
    private EditText buscar;
    private TextView noLibros;

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
        progressBar=view.findViewById(R.id.progressLibros);
        buscar = view.findViewById(R.id.buscarLibro);
        noLibros = view.findViewById(R.id.noLibros);

        libroAdapter = new LibroAdapter(listalibros);
        recyclerView.setAdapter(libroAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        controlEpub.obtenerMisLibros(libroAdapter, listalibros, progressBar, buscar, noLibros, LibrosFragment.this);

        buscarLibros();

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

    public void buscarLibros(){
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                libroAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}