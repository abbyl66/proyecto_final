package com.example.epubook.vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.epubook.R;
import com.example.epubook.controlador.ControlDeslizar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlantillaDeslizar extends AppCompatActivity {

    ViewPager deszContenido;
    LinearLayout indPuntos;
    ControlDeslizar ctrlDeslizar;
    Button botonAtras, botonOmitir, botonSiguiente;
    TextView[] puntos;

    ViewPager.OnPageChangeListener viewPagerList = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setDotIndicator(position);

            //Control de pantalla deslizar.

            //Si la posición es mayor a cero, será posible mostrar el botón atrás.
            if (position > 0){
                botonAtras.setVisibility(View.VISIBLE);
            }else{
                botonAtras.setVisibility(View.INVISIBLE);
            }
            //Si la posición concluye en 2, termina el contenido y se muestra el boton fin.
            if (position == 2){
                botonSiguiente.setText("Fin");
            //De lo contrario, si no ha llegado al 2 puede seguir dando a siguiente.
            }else{
                botonSiguiente.setText("Siguiente");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plantilla_deslizar);

        botonAtras = findViewById(R.id.bt_atras);
        botonSiguiente = findViewById(R.id.bt_siguiente);
        botonOmitir = findViewById(R.id.bt_omitir);

        //Compruebo si la sesión está iniciada para redirigir a la pantalla inicio.
        FirebaseAuth usuario = FirebaseAuth.getInstance();
        if(usuario.getCurrentUser()!=null){

            Intent intent = new Intent(PlantillaDeslizar.this, PantallaInicio.class);
            startActivity(intent);
            finish();

        }


        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Al contenido obtenido se pide el anterior -1.
                if(getItem(0)>0){
                    deszContenido.setCurrentItem(getItem(-1), true);
                }
            }
        });

        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si aún queda contenido que mostrar.
                if(getItem(0)<2) {
                    deszContenido.setCurrentItem(getItem(1), true);

                //Si termina el contenido de la intro, redigire a la pantalla de empezar.
                }else{
                    Intent intent = new Intent(PlantillaDeslizar.this, PantallaEmpezar.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //Redirige a la pantalla de inicio de sesión.
        botonOmitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlantillaDeslizar.this, InicioSesion.class);
                startActivity(intent);
                finish();
            }
        });

        deszContenido = (ViewPager) findViewById(R.id.contenido_intro);
        indPuntos = (LinearLayout) findViewById(R.id.indicador);

        ctrlDeslizar = new ControlDeslizar(this);
        deszContenido.setAdapter(ctrlDeslizar);

        setDotIndicator(0);
        deszContenido.addOnPageChangeListener(viewPagerList);

    }

    public void setDotIndicator(int position){
        puntos = new TextView[3];
        indPuntos.removeAllViews();

        //Puntos indicadores que están en la parte inferior.
        for (int i = 0; i< puntos.length; i++){
            puntos[i] = new TextView(this);
            puntos[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            puntos[i].setTextSize(35);
            puntos[i].setTextColor(getResources().getColor(R.color.grey, getApplicationContext().getTheme()));
            indPuntos.addView(puntos[i]);
        }

        puntos[position].setTextColor(getResources().getColor(R.color.celeste, getApplicationContext().getTheme()));
    }

    private int getItem(int i){
        return deszContenido.getCurrentItem() + i;
    }
}