package com.example.epubook.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.epubook.R;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.LibroAdapter;

import java.util.ArrayList;
import java.util.List;

public class LibrosFragment extends Fragment {

    private RecyclerView recyclerView;
    private LibroAdapter libroAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_libros, container, false);

        recyclerView = view.findViewById(R.id.libroRecyclerView);

        List<Libro> listalibros = new ArrayList<>();
        listalibros.add(new Libro("Primer libro", "Autor"));
        listalibros.add(new Libro("Segundo libro", "Autor"));

        libroAdapter = new LibroAdapter(listalibros);
        recyclerView.setAdapter(libroAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}