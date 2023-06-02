package com.example.epubook.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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

import java.util.ArrayList;
import java.util.List;

public class ColeccionesFragment extends Fragment {

    List<Coleccion> listaColeccion = new ArrayList<>();
    private List<Libro> listaLibros = new ArrayList<>();
    private LibroColeccAdapter libroColeccAdapter;

    public static RecyclerView recyclerView, recyclerCotenido;
    private ColeccAdapter coleccAdapter;

    ControlColecciones controlColecciones;
    ControlDialogos controlDialogos;

    private TextView noColecc, titulo, titulo2, noLibrosColecc;
    private ImageView imgColecc, contenedor;

    private LinearLayout toolbarInicio, toolbar;
    private boolean unClick = true;
    final  int dobleClickTiempo = 700;
    Handler handler = new Handler();
    Runnable runnable;

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
        noLibrosColecc = view.findViewById(R.id.noLibrosColecc);

        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fragcolecc);
        imgColecc.startAnimation(animation);
        titulo.startAnimation(animation);
        titulo2.startAnimation(animation);

        coleccAdapter = new ColeccAdapter(listaColeccion);
        recyclerView.setAdapter(coleccAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        libroColeccAdapter = new LibroColeccAdapter(listaLibros);
        recyclerCotenido.setAdapter(libroColeccAdapter);
        recyclerCotenido.setLayoutManager(new LinearLayoutManager(getActivity()));

        controlColecciones.mostrarColecciones(listaColeccion, coleccAdapter);

        coleccAdapter.setOnColeccListener(new ColeccAdapter.OnColeccClick() {
            @Override
            public void onColeccClick(int pos) {

                if(unClick) {

                    unClick = false;

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            unClick = true;
                        }
                    };

                    //Tiempo que no funcionará el click.
                    handler.postDelayed(runnable, dobleClickTiempo);

                    //Al pulsar alguna coleccion, oculto titulos e imagen de portada.Y control del toolbar eliminar.
                    titulo.setVisibility(View.GONE);
                    titulo2.setVisibility(View.GONE);
                    imgColecc.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                    toolbarInicio.setVisibility(View.VISIBLE);

                    //Uso un contendor blanco para que el aspecto del fragment colecciones se vea bien.
                    if (pos == 0) {
                        contenedor.setVisibility(View.VISIBLE);
                    } else {
                        contenedor.setVisibility(View.INVISIBLE);
                    }

                    controlColecciones.mostrarContenidoColecc(listaLibros, libroColeccAdapter, listaColeccion.get(pos), getActivity(), noLibrosColecc, recyclerCotenido);

                    libroColeccAdapter.setItemLongClick(new LibroColeccAdapter.ItemLongClick() {
                        @Override
                        public void onItemLongClick(View view, int posLibro) {
                            Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.anim_itemc);
                            view.startAnimation(anim);

                            toolbarEliminarItem(posLibro, listaColeccion.get(pos), view);
                        }
                    });
                }

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
        ImageButton atras = requireActivity().findViewById(R.id.cancelarToolbar);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlDialogos.dialogoEliminarLibroColecc(view, posLibro, listaLibros, libroColeccAdapter, coleccion, ColeccionesFragment.this);
                toolbar.setVisibility(View.GONE);
                toolbarInicio.setVisibility(View.VISIBLE);
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    toolbarInicio.setVisibility(View.VISIBLE);
                }
            }
        });


    }

}

