package com.example.lab5;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5.entity.Comida;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResumenActivity extends AppCompatActivity {

    private TableLayout tableComidas;
    private TableLayout tableEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        tableComidas = findViewById(R.id.tableComidas);
        tableEjercicios = findViewById(R.id.tableEjercicios);

        cargarDatos();

    }

    private void cargarDatos() {
        List<Comida> datos = leerDatosDeArchivo(); // Leer ambas comidas y ejercicios

        // Cargar datos
        for (Comida dato : datos) {
            if (!dato.isEsComida()) {
                agregarFilaEjercicio(dato.getNombre(), dato.getCalorias(), dato.getHora());
            } else {
                agregarFilaComida(dato.getNombre(), dato.getCalorias(), dato.getHora());
            }
        }
    }

    private List<Comida> leerDatosDeArchivo() {
        List<Comida> datos = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("comidas.dat");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] datosLinea = linea.split(",");
                String nombre = datosLinea[0];
                int calorias = Integer.parseInt(datosLinea[1]);
                String hora = datosLinea[2];
                boolean esEjercicio = Boolean.parseBoolean(datosLinea[3]); // Leer el diferenciador

                datos.add(new Comida(nombre, calorias, hora, esEjercicio));
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
    }

    private void agregarFilaComida(String nombre, int calorias, String hora) {
        TableRow fila = new TableRow(this);
        TextView textNombre = new TextView(this);
        TextView textCalorias = new TextView(this);
        TextView textHora = new TextView(this);

        textNombre.setText(nombre);
        textCalorias.setText(String.valueOf(calorias));
        textHora.setText(hora);

        fila.addView(textNombre);
        fila.addView(textCalorias);
        fila.addView(textHora);
        tableComidas.addView(fila);
    }

    private void agregarFilaEjercicio(String nombre, int calorias, String hora) {
        TableRow fila = new TableRow(this);
        TextView textNombre = new TextView(this);
        TextView textCalorias = new TextView(this);
        TextView textHora = new TextView(this);

        textNombre.setText(nombre);
        textCalorias.setText(String.valueOf(calorias));
        textHora.setText(hora);

        fila.addView(textNombre);
        fila.addView(textCalorias);
        fila.addView(textHora);
        tableEjercicios.addView(fila);
    }

}