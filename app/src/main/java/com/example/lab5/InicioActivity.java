package com.example.lab5;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.lab5.entity.Comida;
import com.example.lab5.entity.Usuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class InicioActivity extends AppCompatActivity {

    private TextView textInfo, textCaloriasDiarias, textCaloriasConsumidas, textCaloriasRestantes;
    private final String CHANNEL_ID = "notificacion_channel";
    private Handler handler;
    private Runnable runnable;
    private int interval = 10 * 1000;

    private int caloriasRestantes;
    private int caloriasDiariasConEjercicioExtra;

    private EditText editTextComidaNombre, editTextComidaCalorias;
    private Button buttonAgregarComida;
    private int caloriasConsumidas = 0;
    private ArrayList<Comida> listaComidas;

    private EditText editTextEjercicioNombre, editTextEjercicioCalorias;
    private Button buttonAgregarEjercicio;

    String[] mensajesMotivacionales = {
            "Cada registro cuenta. ¡Vuelve a anotar tus comidas!",
            "Tu progreso es importante. ¡Registra tus comidas hoy!",
            "Tu viaje hacia una mejor salud continúa. ¡Regresa ya!",
            "Recuerda, cada comida cuenta. ¡Ingresa y sigue registrando!",
            "La consistencia es clave. ¡No olvides actualizar tu registro!",
            "Cada pequeño paso te acerca a tus objetivos. ¡Vuelve y sigue!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Crear el canal de notificación
        crearCanalNotificacion();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }

        // Configurar y iniciar el envío de notificaciones cada 10 segundos
        iniciarNotificaciones();

        // Inicializando Variables
        textInfo = findViewById(R.id.textInfo);
        textCaloriasDiarias = findViewById(R.id.textCaloriasDiarias);
        textCaloriasRestantes = findViewById(R.id.textCaloriasRestantes);
        Button buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion);

        editTextEjercicioNombre = findViewById(R.id.editTextEjercicioNombre);
        editTextEjercicioCalorias = findViewById(R.id.editTextEjercicioCalorias);
        buttonAgregarEjercicio = findViewById(R.id.buttonAgregarEjercicio);
        Button buttonVerResumen = findViewById(R.id.buttonVerResumen);

        buttonVerResumen.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, ResumenActivity.class);
            startActivity(intent);
        });


        //

        // Recuperar el objeto Usuario
        Usuario usuario = recuperarUsuario();

        if (usuario != null) {
            // Mostrar datos en los TextView
            textInfo.setText("Peso: " + usuario.getPeso() + "kg | Altura: " + usuario.getAltura() + "cm | Edad: " + usuario.getEdad() + "\nGénero: " + usuario.getGenero());

            double gastoCalorico = Double.parseDouble(usuario.getGastoCalorico());
            int gastoCaloricoInt = (int) Math.round(gastoCalorico);
            caloriasDiariasConEjercicioExtra = gastoCaloricoInt;
            textCaloriasDiarias.setText("Calorias diarias: " + caloriasDiariasConEjercicioExtra);

            caloriasRestantes = gastoCaloricoInt;

        } else {
            textInfo.setText("No hay datos del usuario.");
            textCaloriasDiarias.setText("No disponible.");
        }

        // Configurar el botón de cerrar sesión
        buttonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Borrar datos del usuario
                File userFile = new File(getFilesDir(), "usuario.dat");
                if (userFile.exists()) {
                    userFile.delete(); // Elimina el archivo de usuario
                }

                // Borrar datos de comida
                File foodFile = new File(getFilesDir(), "comidas.dat");
                if (foodFile.exists()) {
                    foodFile.delete(); // Elimina el archivo de comidas
                }

                // Redirigir al MainActivity
                Intent intent = new Intent(InicioActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editTextComidaNombre = findViewById(R.id.editTextComidaNombre);
        editTextComidaCalorias = findViewById(R.id.editTextComidaCalorias);
        textCaloriasConsumidas = findViewById(R.id.textCaloriasConsumidas);
        buttonAgregarComida = findViewById(R.id.buttonAgregarComida);

        // Cargar datos previos si existen
        cargarDatos();

        buttonAgregarComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén los valores de los EditText
                String comidaNombre = editTextComidaNombre.getText().toString();
                String caloriasComida = editTextComidaCalorias.getText().toString();

                // Asegúrate de que ambos campos no estén vacíos
                if (!comidaNombre.isEmpty() && !caloriasComida.isEmpty()) {
                    agregarComida();

                    editTextComidaNombre.setText("");
                    editTextComidaCalorias.setText("");
                } else {
                    Toast.makeText(InicioActivity.this, "Por favor ingresa la comida y las calorías.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAgregarEjercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ejercicioNombre = editTextEjercicioNombre.getText().toString();
                String caloriasEjercicio = editTextEjercicioCalorias.getText().toString();

                if (!ejercicioNombre.isEmpty() && !caloriasEjercicio.isEmpty()) {
                    agregarEjercicio();

                    // Limpiar los campos de texto después de agregar
                    editTextEjercicioNombre.setText("");
                    editTextEjercicioCalorias.setText("");
                } else {
                    Toast.makeText(InicioActivity.this, "Por favor ingresa el ejercicio y las calorías.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void agregarComida() {
        String nombreComida = editTextComidaNombre.getText().toString();
        int caloriasComida = Integer.parseInt(editTextComidaCalorias.getText().toString());
        Date horaComida = new Date();

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Comida comida = new Comida(nombreComida, caloriasComida, formato.format(horaComida), true);
        listaComidas.add(comida);
        caloriasConsumidas += caloriasComida;
        textCaloriasConsumidas.setText("Calorías consumidas: " + caloriasConsumidas);
        int caloriasComsumidasInt = caloriasConsumidas;

        if(caloriasRestantes - caloriasComsumidasInt > 0){
            int caloriasRestantesInt = caloriasRestantes - caloriasComsumidasInt;
            textCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantesInt);
            guardarDatos();
        } else{
            mostrarNotificacionExcesoCalorias();

            textCaloriasRestantes.setText("Calorías restantes: 0");
            guardarDatos();
        }
    }

    private void agregarEjercicio() {
        String nombreEjercicio = editTextEjercicioNombre.getText().toString();
        int caloriasEjercicio = Integer.parseInt(editTextEjercicioCalorias.getText().toString());
        Date horaEjercicio = new Date();

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Comida ejercicio = new Comida(nombreEjercicio, caloriasEjercicio, formato.format(horaEjercicio), false);
        listaComidas.add(ejercicio);

        if(caloriasRestantes - caloriasEjercicio > 0 && caloriasDiariasConEjercicioExtra - caloriasEjercicio > 0){

            caloriasRestantes = caloriasRestantes - caloriasEjercicio;
            caloriasDiariasConEjercicioExtra = caloriasDiariasConEjercicioExtra - caloriasEjercicio;

            textCaloriasDiarias.setText("Calorias diarias: " + caloriasDiariasConEjercicioExtra);
            textCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantes);
            guardarDatos();
        } else{
            mostrarNotificacionExcesoEjercicio();

            textCaloriasRestantes.setText("Calorías restantes: 0");
            guardarDatos();
        }


    }

    private void guardarDatos() {
        try {
            // Guardar comidas
            FileOutputStream fos = openFileOutput("comidas.dat", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(listaComidas);
            oos.writeInt(caloriasConsumidas);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDatos() {
        try {
            FileInputStream fis = openFileInput("comidas.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            listaComidas = (ArrayList<Comida>) ois.readObject();
            caloriasConsumidas = ois.readInt(); // Leer calorías consumidas
            ois.close();
            fis.close();
            textCaloriasConsumidas.setText("Calorías consumidas: " + caloriasConsumidas);

            int caloriasComsumidasInt = caloriasConsumidas;
            int caloriasRestantesInt = caloriasRestantes - caloriasComsumidasInt;
            textCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantesInt);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            listaComidas = new ArrayList<>(); // Inicializar lista vacía si no hay datos
            textCaloriasConsumidas.setText("Calorías consumidas: 0");
            textCaloriasRestantes.setText("Calorías restantes: " + caloriasRestantes);
        }
    }

    private Usuario recuperarUsuario() {
        Usuario usuario = null;
        try {
            FileInputStream fis = openFileInput("usuario.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            usuario = (Usuario) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    private void crearCanalNotificacion() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Canal de Notificación";
            String description = "Canal para notificaciones de TeleFit";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void iniciarNotificaciones() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                enviarNotificacion();
                handler.postDelayed(this, interval);
            }
        };
        handler.postDelayed(runnable, interval); // Inicia después de 10 segundos
    }

    private void enviarNotificacion() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Seleccionar un mensaje aleatorio
        Random random = new Random();
        String mensajeAleatorio = mensajesMotivacionales[random.nextInt(mensajesMotivacionales.length)];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("TELEFIT")
                .setContentText(mensajeAleatorio)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }

    private void mostrarNotificacionExcesoCalorias() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener un icono para la notificación
                    .setContentTitle("¡Exceso de Calorías!")
                .setContentText("Has excedido tu límite de calorías diarias.")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta para que sea notoria
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(2, builder.build()); // Utiliza un ID diferente para esta notificación
        }
    }

    private void mostrarNotificacionExcesoEjercicio() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener un icono para la notificación
                .setContentTitle("¡Exceso de Ejercicio!")
                .setContentText("Estas consumiendo muchas calorias de tu dia.")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta para que sea notoria
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(2, builder.build()); // Utiliza un ID diferente para esta notificación
        }
    }

}