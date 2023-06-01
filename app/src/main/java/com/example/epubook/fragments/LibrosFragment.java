package com.example.epubook.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.controlador.ControlEpub;
import com.example.epubook.modelo.Libro;
import com.example.epubook.vista.DeslizarCardView;
import com.example.epubook.vista.LectorEpub;
import com.example.epubook.vista.LibroAdapter;

import java.util.ArrayList;
import java.util.List;

public class LibrosFragment extends Fragment{

    List<Libro> listalibros = new ArrayList<>();
    private ControlDialogos controlDialogos;

    private RecyclerView recyclerView;
    private ControlEpub controlEpub;
    private  LibroAdapter libroAdapter;

    private ProgressBar progressBar;
    private EditText buscar;
    private TextView noLibros;
    private Button buscarBoton;

    private LinearLayout toolbar, toolbarInicio;

    Animation animEditxtIzq, animEditxtDer;


    @SuppressLint("ResourceType")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controlEpub = new ControlEpub(context);
        controlDialogos = new ControlDialogos(context);
        animEditxtIzq = AnimationUtils.loadAnimation(context, R.anim.anim_edittext_izq);
        animEditxtDer = AnimationUtils.loadAnimation(context, R.anim.anim_edittext_der);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_libros, container, false);

        toolbar  = requireActivity().findViewById(R.id.toolbarEliminar);
        toolbarInicio  = requireActivity().findViewById(R.id.toolbarInicio);

        toolbar.setVisibility(View.GONE);
        toolbarInicio.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.libroRecyclerView);
        progressBar=view.findViewById(R.id.progressLibros);
        buscar = view.findViewById(R.id.buscarLibro);
        noLibros = view.findViewById(R.id.noLibros);
        buscarBoton = view.findViewById(R.id.buscarBoton);

        libroAdapter = new LibroAdapter(listalibros);
        recyclerView.setAdapter(libroAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        libroAdapter.setOnItemClickListener(new LibroAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                String ruta = libroAdapter.getLibrosFiltro().get(pos).getRuta();
                Intent intent = new Intent(requireContext(), LectorEpub.class);
                intent.putExtra("ruta", ruta);
                requireContext().startActivity(intent);
            }
        });

        controlEpub.obtenerMisLibros(libroAdapter, listalibros, progressBar, noLibros, LibrosFragment.this);

        buscarLibros();

        //Barra de búsqueda de libros.
        buscarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Controlo posibles errores si se mantiene visible o no.
                if(buscar.getVisibility() == View.VISIBLE){
                    animEditxtDer.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(buscar.getText().toString().isEmpty()){
                                buscar.setVisibility(View.INVISIBLE);
                            }else{
                                buscar.setText("");
                                buscar.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    buscar.startAnimation(animEditxtDer);
                }else{
                    buscar.startAnimation(animEditxtIzq);
                    buscar.setVisibility(View.VISIBLE);
                }
            }
        });

        //Deslizo para eliminar libro.
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DeslizarCardView(LibrosFragment.this, libroAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

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

    public void eliminarEpub(LibrosFragment librosFragment, int pos) {
        controlDialogos.dialogoEliminarItem(librosFragment.getView(), pos, listalibros, libroAdapter, noLibros);
    }
}