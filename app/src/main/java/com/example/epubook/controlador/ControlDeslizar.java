package com.example.epubook.controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.epubook.R;

public class ControlDeslizar extends PagerAdapter {

    Context context;

    //Imágenes, títulos y descripciones que usaré para cada pantalla de intro.
    int imagenes[] = {R.drawable.bienvenidos, R.drawable.basedatos, R.drawable.librosintro};
    int titulos[] = {R.string.slider1, R.string.slider2, R.string.slider3};
    int descripciones[] = {R.string.desc1, R.string.desc2, R.string.desc3};


    public ControlDeslizar(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return titulos.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    //Método que da formato a las distintas pantallas que se generarán las variables antes recogidas.
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.plantilla_info, container, false);

        ImageView deszImg = (ImageView) view.findViewById(R.id.img_intro);
        TextView deszTitulo = (TextView) view.findViewById(R.id.titulo_intro);
        TextView deszDesc = (TextView) view.findViewById(R.id.desc_intro);

        deszImg.setImageResource(this.imagenes[position]);
        deszTitulo.setText(this.titulos[position]);
        deszDesc.setText(this.descripciones[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
