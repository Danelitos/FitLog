package com.example.proyecto_das;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    EditText editTextUsuario, editTextPassword;
    Button botonInicio, botonRegistro;
    BaseDatos db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cambiarIdioma(); // Cambia el idioma de la aplicación según la configuración guardada
        setContentView(R.layout.activity_login); // Establece el diseño de la actividad

        // Inicializa los elementos de la interfaz de usuario y la base de datos
        editTextUsuario = findViewById(R.id.usuario);
        editTextPassword = findViewById(R.id.password);
        db = new BaseDatos(this);

        // Asigna acciones a los botones de inicio de sesión y registro
        botonInicio = findViewById(R.id.botonIniciar);
        botonRegistro = findViewById(R.id.botonRegistro);

        botonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica el inicio de sesión con los datos proporcionados
                String username = editTextUsuario.getText().toString();
                String password = editTextPassword.getText().toString();
                if (db.verificarLogin(username, password)) {
                    // Si las credenciales son válidas, inicia la actividad principal y pasa el nombre de usuario
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("usuario", username);
                    startActivity(i);
                } else {
                    // Si las credenciales son incorrectas, muestra un mensaje de error
                    Toast.makeText(LoginActivity.this, R.string.ts_creden_incorrectas, Toast.LENGTH_SHORT).show();
                }
            }
        });

        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de registro
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });

        cambiarColorBotones(); // Cambia el color de los botones según la configuración guardada
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restablece los campos de usuario y contraseña cuando la actividad se reanuda
        editTextUsuario.setText("");
        editTextPassword.setText("");
    }

    // Método para cambiar el idioma de la aplicación
    private void cambiarIdioma() {
        // Obtener el idioma guardado en las preferencias compartidas
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idiomaGuardado = prefs.getString("idioma", "");

        // Obtener el código de idioma correspondiente al idioma guardado
        String[] idiomasValues = getResources().getStringArray(R.array.idiomas_values);
        int index = Arrays.asList(getResources().getStringArray(R.array.idiomas)).indexOf(idiomaGuardado);
        String codigoIdioma = idiomasValues[index];

        // Cambiar el idioma si es diferente del idioma actual
        if (!codigoIdioma.equals(getResources().getConfiguration().locale.getLanguage())) {
            Locale locale = new Locale(codigoIdioma);
            Locale.setDefault(locale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    // Método para cambiar el color de los botones según la configuración guardada
    private void cambiarColorBotones() {
        // Obtener el color guardado en las preferencias
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
        // Asignar el color a los botones de inicio de sesión y registro
        botonInicio.setBackgroundColor(getResources().getColor(colorId));
        botonRegistro.setBackgroundColor(getResources().getColor(colorId));
    }
}
