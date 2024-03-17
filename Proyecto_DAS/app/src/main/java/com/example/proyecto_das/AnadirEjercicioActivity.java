package com.example.proyecto_das;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;


public class AnadirEjercicioActivity extends AppCompatActivity implements DialogoActivity.ListenerdelDialogoAnadirEjercicio, DialogoActivity.ListenerdelDialogoMain {

    private EditText nombreEjer, descripEjer;
    private BaseDatos db;
    private String usuario;
    private Button botonAnadir,botonSeleccionarImagen;
    private ImageView imagen;
    private Uri imagenUri;
    private boolean imagenElegida=false;

    private static final int DIALOGO_CERRAR_SESION = 1;
    private static final int DIALOGO_ANADIR_EJERCICIO = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cambiarIdioma(); // Cambia el idioma de la aplicación según la configuración guardada
        setContentView(R.layout.activity_anadirejercicio);
        setSupportActionBar(findViewById(R.id.labarra));

        imagen = findViewById(R.id.imageView2);

        // Restaurar la URI de la imagen seleccionada si hay un estado guardado
        if (savedInstanceState != null) {
            imagenElegida = savedInstanceState.getBoolean("imagenElegida");
            if (imagenElegida) {
                String uri = savedInstanceState.getString("imagenUri");
                imagenUri = Uri.parse(uri);
                if (imagenUri != null) {
                    imagen.setImageURI(imagenUri);
                }
            }
        }



        // Obtener el nombre de usuario pasado en el intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario= extras.getString("usuario");
        }

        db = new BaseDatos(this);
        nombreEjer = findViewById(R.id.nombreEjer);
        descripEjer = findViewById(R.id.descripEjer);
        botonAnadir = findViewById(R.id.botonAnadir);
        botonSeleccionarImagen = findViewById(R.id.botonSeleccionarImagen);

        botonAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alPulsarAnadir();
            }
        });

        botonSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la galería de imágenes para seleccionar una imagen
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });

        // Cambiar el color de los botones según la preferencia del usuario
        cambiarColorBotones();

    }

    // Método para guardar el estado de la actividad para que al cambiar la orientacion no se pierda la imagen
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Guardar la URI de la imagen seleccionada
        if (imagenUri != null) {
            outState.putString("imagenUri", imagenUri.toString());
            outState.putBoolean("imagenElegida", true);
        }
    }

    // METODOS PARA LA LOGICA DEL ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_anadir_ejercicio,menu);
        return true;
    }

    // Método para gestionar las acciones del menú de opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ajustes) {
            // Abrir la actividad de ajustes
            finish();
            Intent intent = new Intent(AnadirEjercicioActivity.this,AjustesActivity.class);
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

    // Método que se ejecuta cuando se ha seleccionado una imagen desde la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {

            // Obtener la ruta real de la imagen
            String realUri = getRealPathFromURI(data.getData());
            imagenUri = Uri.parse(realUri);

            // Mostrar la imagen
            imagen.setImageURI(imagenUri);
        }
    }

    // Método para obtener la ruta real de la imagen a partir de su URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
    }

    // Método para gestionar la pulsación del botón de añadir ejercicio
    @Override
    public void alPulsarAnadir() {
        DialogoActivity dialogoNivel = new DialogoActivity();
        Bundle args = new Bundle();
        args.putInt("tipoDialogo", DIALOGO_ANADIR_EJERCICIO);
        dialogoNivel.setArguments(args);
        dialogoNivel.miListenerAnadirEjercicioActivity = this; // Establecer el listener
        dialogoNivel.show(getSupportFragmentManager(), "dialogo_anadirEjercicio");
    }

    // Método para añadir un ejercicio a la base de datos
    @Override
    public void alPulsarAceptarAnadirEjercicio() {
        String nombre = nombreEjer.getText().toString();
        String descripcion = descripEjer.getText().toString();

        int usuarioId=db.obtenerIdUsuario(usuario);
        db.agregarEjercicio(db.getWritableDatabase(),usuarioId,new Ejercicio(nombre,descripcion,0,String.valueOf(imagenUri)));
        finish();
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
        Intent intent = new Intent(AnadirEjercicioActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Para resetear la pila de intens
        startActivity(intent);
        finish();
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

        // Aplicar el color a los botones
        botonAnadir = findViewById(R.id.botonAnadir);
        botonSeleccionarImagen = findViewById(R.id.botonSeleccionarImagen);
        botonAnadir.setBackgroundColor(getResources().getColor(colorId));
        botonSeleccionarImagen.setBackgroundColor(getResources().getColor(colorId));
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
