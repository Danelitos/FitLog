package com.example.proyecto_das;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DialogoActivity.ListenerdelDialogoMain{
    private ArrayList<Ejercicio> listaEjercicios;
    private String usuario;
    private static final int DIALOGO_CERRAR_SESION = 1;
    private BaseDatos db;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cambiarIdioma(); // Cambia el idioma de la aplicación según la configuración guardada

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.labarra));

        // Obtener el nombre de usuario pasado en el intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario= extras.getString("usuario");
        }

        // Inicializar la lista de ejercicios desde la base de datos
        db = new BaseDatos(this);
        int idUsuario = db.obtenerIdUsuario(usuario);
        listaEjercicios = db.obtenerTodosEjercicios(idUsuario);


        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEjercicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new EjercicioAdapter(this, listaEjercicios, usuario));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_descargar) {
            guardarEjerciciosEnFichero();
            return true;
        } else if (id == R.id.action_anadir_ejercicio) {
            Intent intent = new Intent(MainActivity.this,AnadirEjercicioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
            return true;
        } else if (id == R.id.ajustes) {
            finish();
            Intent intent = new Intent(MainActivity.this,AjustesActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
            return true;
        } else if (id == R.id.cerrarSesion) {
            alPulsarCerrarSesion();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void alPulsarCerrarSesion() {
        // Mostrar el diálogo para confirmar el cierre de sesión
        DialogoActivity dialogoNivel = new DialogoActivity();
        Bundle args = new Bundle();
        args.putInt("tipoDialogo", DIALOGO_CERRAR_SESION);
        dialogoNivel.setArguments(args);
        dialogoNivel.miListenerMainActivity = this; // Establecer el listener
        dialogoNivel.show(getSupportFragmentManager(), "dialogo_cerrarSesion");
    }

    // Método llamado cuando el usuario confirma el cierre de sesión en el diálogo
    @Override
    public void alPulsarAceptarCerrarSesion() {
        // Cerrar la sesión y finalizar la actividad
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Para resetear la pila de intens
        startActivity(intent);
        finish();
    }

    // PARA QUE CUANDO SE AÑADA UN EJERCICIO NUEVO SE ACTUALICE
    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar la lista de ejercicios
        actualizarListaEjercicios();
    }

    // Método para actualizar la lista de ejercicios mostrada en RecyclerView
    private void actualizarListaEjercicios() {
        int idUsuario = db.obtenerIdUsuario(usuario);
        listaEjercicios.clear(); // Limpiar la lista actual de ejercicios
        listaEjercicios.addAll(db.obtenerTodosEjercicios(idUsuario)); // Obtener los ejercicios actualizados
        RecyclerView recyclerView = findViewById(R.id.recyclerViewEjercicios);
        recyclerView.getAdapter().notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    // Método para guardar los ejercicios del usuario en un archivo
    private void guardarEjerciciosEnFichero() {
        // Obtener los ejercicios del usuario de la base de datos
        int usuarioId = db.obtenerIdUsuario(usuario);
        ArrayList<Ejercicio> ejerciciosUsuario = db.obtenerTodosEjercicios(usuarioId);

        // Crear una representación en texto de los ejercicios
        StringBuilder sb = new StringBuilder();
        for (Ejercicio ejercicio : ejerciciosUsuario) {
            sb.append(ejercicio.getNombre()).append(": ").append(ejercicio.getDescripcion()).append("\n").append("Valoracion: ").append(ejercicio.getValoracion()).append("\n").append("\n");
        }

        // Guardar la representación en un archivo en el directorio "Downloads"
        String texto = getResources().getString(R.string.txt_ejercicios);
        String fileName = texto + usuario + ".txt";
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(sb.toString());
            writer.flush();
            writer.close();
            texto = getResources().getString(R.string.txt_guardado);
            Toast.makeText(this, texto + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.txt_error, Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cambiar el idioma de la aplicación
    private void cambiarIdioma() {
        // Obtener el idioma guardado en las preferencias compartidas
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idiomaGuardado = prefs.getString("idioma", "");
        String codigoIdioma;

        // Si no se ha guardado ningún idioma, utilizar el valor por defecto definido en los recursos
        if (idiomaGuardado.isEmpty()) {
            codigoIdioma = "es";
        }
        else{
            // Obtener el código de idioma correspondiente al idioma guardado
            String[] idiomasValues = getResources().getStringArray(R.array.idiomas_values);
            int index = Arrays.asList(getResources().getStringArray(R.array.idiomas)).indexOf(idiomaGuardado);
            codigoIdioma = idiomasValues[index];
        }

        // Cambiar el idioma si es diferente del idioma actual
        if (!codigoIdioma.equals(getResources().getConfiguration().locale.getLanguage())) {
            Locale locale = new Locale(codigoIdioma);
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }
}
