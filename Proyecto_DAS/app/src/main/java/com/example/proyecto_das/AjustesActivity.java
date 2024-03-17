package com.example.proyecto_das;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;

public class AjustesActivity extends AppCompatActivity implements DialogoActivity.ListenerdelDialogoMain {

    private Spinner spinnerIdiomas, spinnerColores;
    private static final int DIALOGO_CERRAR_SESION = 1;
    private String usuario;
    private Button btnGuardarCambios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ajustes);
        setSupportActionBar(findViewById(R.id.labarra));


        // Obtener el nombre de usuario pasado en el intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario= extras.getString("usuario");
        }

        // Configurar el spinner de idiomas
        spinnerIdiomas = findViewById(R.id.spinnerIdiomas);
        cargarIdiomas();

        // Configurar el spinner de colores
        spinnerColores = findViewById(R.id.spinnerColores);
        cargarColores();

        // Configurar el botón para guardar los cambios
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar un diálogo de confirmación antes de guardar los cambios
                AlertDialog.Builder builder = new AlertDialog.Builder(AjustesActivity.this);
                builder.setTitle(R.string.dlg_guardarT);
                builder.setMessage(R.string.dlg_guardarQ);
                builder.setPositiveButton(R.string.dlg_Aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Guardar el idioma y el color seleccionados
                        guardarColorSeleccionado();
                        guardarIdiomaSeleccionado();
                    }
                });
                builder.setNegativeButton(R.string.dlg_Cancelar, null);
                builder.create().show();
            }
        });

        // Cambiar el color de los botones según la preferencia del usuario
        cambiarColorBotones();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ajustes,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_anadir_ejercicio) {
            // Ir a la pantalla de añadir ejercicio
            finish();
            Intent intent = new Intent(AjustesActivity.this,AnadirEjercicioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
            return true;
        } else if (id == R.id.cerrarSesion) {
            // Mostrar el diálogo para confirmar el cierre de sesión
            alPulsarCerrarSesion();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // LOGICA PARA CAMBIAR EL IDIOMA DE LA APP

    // Método para cargar los idiomas disponibles en el spinner
    private void cargarIdiomas() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.idiomas,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdiomas.setAdapter(adapter);

        // Establecer el idioma seleccionado previamente si está guardado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idiomaGuardado = prefs.getString("idioma", "");
        if (!idiomaGuardado.isEmpty()) {
            int index = adapter.getPosition(idiomaGuardado);
            spinnerIdiomas.setSelection(index);
        }

        // Listener para guardar el idioma seleccionado cuando cambia
        spinnerIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // No es necesario almacenar el idioma seleccionado aquí,
                // ya que se obtendrá cuando se haga clic en "Guardar cambios"
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // Método para guardar el idioma seleccionado en las preferencias
    private void guardarIdiomaSeleccionado() {

        String idiomaSeleccionado = spinnerIdiomas.getSelectedItem().toString();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("idioma", idiomaSeleccionado);
        editor.apply();

        cambiarIdioma(idiomaSeleccionado);
    }

    // Método para cambiar el idioma de la aplicación
    private void cambiarIdioma(String idioma) {
        // Obtener el código de idioma correspondiente al idioma seleccionado
        String[] idiomasValues = getResources().getStringArray(R.array.idiomas_values);
        int index = Arrays.asList(getResources().getStringArray(R.array.idiomas)).indexOf(idioma);
        String codigoIdioma = idiomasValues[index];
        setLocale(codigoIdioma);

    }

    // Método para establecer el idioma de la aplicación
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);

        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        recreate();
    }

    // LOGICA PARA CAMBIAR LOS BOTONES DE LA APP

    // Método para cargar los colores disponibles en el spinner
    private void cargarColores() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.colores,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColores.setAdapter(adapter);

        // Establecer el color seleccionado previamente si está guardado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String colorGuardado = prefs.getString("botonColor", "");
        if (!colorGuardado.isEmpty()) {
            int index = adapter.getPosition(colorGuardado);
            spinnerColores.setSelection(index);
        }

        // Listener para guardar el color seleccionado cuando cambia
        spinnerColores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // No es necesario almacenar el color seleccionado aquí,
                // ya que se obtendrá cuando se haga clic en "Guardar cambios"
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // Método para guardar el color seleccionado en las preferencias
    private void guardarColorSeleccionado() {
        String colorSeleccionado = spinnerColores.getSelectedItem().toString();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("botonColor", colorSeleccionado);
        editor.apply();
    }

    // Método para cambiar el color de los botones según la preferencia del usuario
    private void cambiarColorBotones() {
        // Obtener el idioma guardado en las preferencias
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String color = prefs.getString("botonColor", "");
        int colorId;
        switch (color) {
            case "Por defecto":
                colorId = R.color.defecto;
                break;
            case "Verde":
                colorId = R.color.Verde;
                break;
            case "Azul":
                colorId = R.color.Azul;
                break;
            default:
                colorId = R.color.defecto;
        }
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnGuardarCambios.setBackgroundColor(getResources().getColor(colorId));
    }


    // Método llamado cuando se pulsa el botón de cerrar sesión
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
        Intent intent = new Intent(AjustesActivity.this, LoginActivity.class); // Cambia LoginActivity por la actividad de inicio de sesión real
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Se llama a este metodo cuando el usuario pincha atras en la actividad.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(AjustesActivity.this, MainActivity.class);
        i.putExtra("usuario", usuario);
        startActivity(i);
    }

}
