package com.example.epubook.vista;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlDialogos;
import com.example.epubook.fragments.LibrosFragment;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DeslizarCardView extends ItemTouchHelper.SimpleCallback {

    private LibroAdapter libroAdapter;
    LibrosFragment librosFragment;

    public DeslizarCardView(LibrosFragment librosFragment, LibroAdapter libroAdapter){
        super(0, ItemTouchHelper.LEFT | 0   );
        this.libroAdapter = libroAdapter;
        this.librosFragment = librosFragment;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getPosition();
        if(direction == ItemTouchHelper.LEFT){
            librosFragment.eliminarEpub(librosFragment, pos);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View view = viewHolder.itemView;

        //Fondo purpura si se desliza a la izquierda, eliminar.
        if(dX < 0){
            Drawable fondo = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.item_fondo);
            fondo.setBounds(view.getLeft() + (int) dX, view.getTop(), view.getRight(), view.getBottom());
            fondo.draw(c);
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive).addActionIcon(R.drawable.ic_baseline_delete_24).create().decorate();
        }

    }
}
