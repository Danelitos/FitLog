package com.example.proyecto_das;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistroActivity extends AppCompatActivity {
    EditText editTextUsuario, editTextPassword;
    Button botonInicio, botonRegistro;
    BaseDatos dbUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro); // Establece el diseño de la actividad

        // Inicializa los elementos de la interfaz de usuario y la base de datos
        editTextUsuario = findViewById(R.id.usuario2);
        editTextPassword = findViewById(R.id.password2);
        dbUsu = new BaseDatos(this);

        // Asigna acciones a los botones de registro e inicio de sesión
        botonInicio = findViewById(R.id.botonInicio2);
        botonRegistro = findViewById(R.id.botonRegistro2);

        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene el nombre de usuario y la contraseña introducidos por el usuario
                String usuario = editTextUsuario.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Verifica si se han dejado campos vacíos
                if (usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, R.string.ts_informar_campos, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifica si la contraseña tiene al menos 6 caracteres
                if (password.length() < 6) {
                    Toast.makeText(RegistroActivity.this, R.string.ts_passLongitud, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifica si el usuario ya existe en la base de datos
                if (dbUsu.verificarUsuarioExiste(usuario)) {
                    Toast.makeText(RegistroActivity.this, R.string.ts_usuario_existe, Toast.LENGTH_SHORT).show();
                } else {
                    // Si el usuario no existe, intenta añadirlo a la base de datos
                    if (dbUsu.anadirUsuario(usuario, password)) {
                        // Si el registro es exitoso, muestra un mensaje de éxito y finaliza la actividad
                        Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        // Envía una notificación de bienvenida al usuario registrado
                        NotificacionActivity.enviarNotificacionBienvenida(RegistroActivity.this, usuario);
                        finish();
                    } else {
                        // Si el registro falla, muestra un mensaje de error
                        Toast.makeText(RegistroActivity.this, "El registro falló", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Acción para el botón de inicio, que simplemente finaliza la actividad actual y vuelve a la anterior (LoginActivity)
        botonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cambiarColorBotones(); // Cambia el color de los botones según la configuración guardada
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
        botonInicio = findViewById(R.id.botonInicio2);
        botonRegistro = findViewById(R.id.botonRegistro2);
        botonInicio.setBackgroundColor(getResources().getColor(colorId));
        botonRegistro.setBackgroundColor(getResources().getColor(colorId));
    }
}
