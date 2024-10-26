package com.example.lab5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5.entity.Usuario;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerGenero, spinnerActFisica, spinnerObjetivo;
    private EditText editTextPeso, editTextAltura, editTextEdad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si ya existe un usuario guardado
        if (usuarioGuardadoExiste()) {
            // Si existe, redirigir a InicioActivity
            Intent intent = new Intent(this, InicioActivity.class);
            startActivity(intent);
            finish(); // Finalizar MainActivity para que no se quede en el stack
        } else {
            setContentView(R.layout.activity_main);

            //Gestion del Formulario

            editTextPeso = findViewById(R.id.editTextPeso);
            editTextAltura = findViewById(R.id.editTextAltura);
            editTextEdad = findViewById(R.id.editTextEdad);

            spinnerGenero = findViewById(R.id.spinnerGenero);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.lista_genero, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGenero.setAdapter(adapter);

            spinnerActFisica = findViewById(R.id.spinnerActFisica);
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                    R.array.lista_nivel_actividad_fisica, android.R.layout.simple_spinner_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerActFisica.setAdapter(adapter1);

            spinnerObjetivo = findViewById(R.id.spinnerObjetivo);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.lista_objetivos, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerObjetivo.setAdapter(adapter2);

            Button btnRegistrar = findViewById(R.id.btnRegistrar);

            btnRegistrar.setOnClickListener(v -> registroDatos());
            //----------------------------------------------------------------------------

        }

    }

    private void registroDatos() {

        String pesoIngresado = editTextPeso.getText().toString();
        String alturaIngresado = editTextAltura.getText().toString();
        String edadIngresado = editTextEdad.getText().toString();
        String tipoGenero = spinnerGenero.getSelectedItem().toString();
        String tipoActFisica = spinnerActFisica.getSelectedItem().toString();
        String tipoObjetivo = spinnerObjetivo.getSelectedItem().toString();

        if (!pesoIngresado.isEmpty() && !alturaIngresado.isEmpty() && !edadIngresado.isEmpty() && !tipoGenero.equals("-Seleccionar-") && !tipoActFisica.equals("-Seleccionar-") && !tipoObjetivo.equals("-Seleccionar-")) {
            double consumoCaloricoDiario = 0.0; //Formula obtenida de: https://www.gob.pe/14903-calcular-tasa-de-metabolismo-basal-tmb-en-adultos
            int peso = Integer.parseInt(pesoIngresado);
            int altura = Integer.parseInt(alturaIngresado);
            int edad = Integer.parseInt(edadIngresado);

            if (tipoGenero.equals("Femenino")) {
                consumoCaloricoDiario = (10 * peso) + (6.25 * altura) - (5 * edad) - 161;
            } else {
                consumoCaloricoDiario = (10 * peso) + (6.25 * altura) - (5 * edad) + 5;
            }

            if(tipoActFisica.equals("Poca")){
                consumoCaloricoDiario = consumoCaloricoDiario * 1.2;
            } else if (tipoActFisica.equals("Ejercicio ligera")) {
                consumoCaloricoDiario = consumoCaloricoDiario * 1.375;
            } else if (tipoActFisica.equals("Ejercicio moderado")) {
                consumoCaloricoDiario = consumoCaloricoDiario * 1.55;
            } else if (tipoActFisica.equals("Ejercicio fuerte")) {
                consumoCaloricoDiario = consumoCaloricoDiario * 1.725;
            } else if (tipoActFisica.equals("Ejercicio muy fuerte")) {
                consumoCaloricoDiario = consumoCaloricoDiario * 1.9;
            }

            if(tipoObjetivo.equals("Subir de peso")){
                consumoCaloricoDiario = consumoCaloricoDiario + 500;
            } else if (tipoObjetivo.equals("Bajar de peso")) {
                consumoCaloricoDiario = consumoCaloricoDiario - 300;
            }

            Usuario usuario = new Usuario(pesoIngresado, alturaIngresado, edadIngresado, tipoGenero, String.valueOf(consumoCaloricoDiario), "0");

            // Guardar el objeto en almacenamiento interno
            guardarUsuario(usuario);

            Intent intent = new Intent(this, InicioActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "¡Debe ingresar todos los datos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarUsuario(Usuario usuario) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("usuario.dat", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(usuario);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean usuarioGuardadoExiste() {
        try {
            FileInputStream fileInputStream = openFileInput("usuario.dat");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Usuario usuario = (Usuario) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return usuario != null;
        } catch (Exception e) {
            return false;
        }
    }

}