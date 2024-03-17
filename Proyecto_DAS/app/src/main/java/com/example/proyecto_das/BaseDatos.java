package com.example.proyecto_das;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BaseDatos extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "fitlog.db";

    // Tabla de Usuarios
    private static final String TABLA_USUARIOS = "usuarios";
    private static final String COL_ID = "ID";
    private static final String COL_USUARIO = "USUARIO";
    private static final String COL_PASSWORD = "PASSWORD";

    // Tabla de Ejercicios
    private static final String TABLA_EJERCICIOS = "ejercicios";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_USER_ID = "ID_USUARIO"; // Foreign key
    private static final String COLUMN_NOMBRE = "NOMBRE";
    private static final String COLUMN_DESCRIPCION = "DESCRIPCION";
    private static final String COLUMN_IMAGEN = "IMAGEN";
    private static final String COLUMN_IMAGEN_URI = "IMAGEN_URI";
    private static final String COLUMN_VALORACION = "VALORACION";

    public BaseDatos(Context context) {
        super(context, NOMBRE_BD, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creacion tabla usuarios
        String tablaUsuarios = "CREATE TABLE " + TABLA_USUARIOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USUARIO + " TEXT, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(tablaUsuarios);

        // Creacion tabla ejercicios
        String tablaEjercicios = "CREATE TABLE " + TABLA_EJERCICIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_IMAGEN + " INTEGER, " +
                COLUMN_IMAGEN_URI + " TEXT, " +
                COLUMN_VALORACION + " FLOAT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " +
                TABLA_USUARIOS + "(" + COL_ID + "))";
        db.execSQL(tablaEjercicios);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_EJERCICIOS);
        onCreate(db);
    }

    // METODOS PARA LA TABLA USUARIOS
    public boolean anadirUsuario(String usuario, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_USUARIO, usuario);
        valores.put(COL_PASSWORD, hashPassword(password));

        // Insertar el nuevo usuario
        long usuarioId = db.insert(TABLA_USUARIOS, null, valores);

        // Verificar si la inserción fue exitosa
        if (usuarioId != -1) {
            // Insertar los ejercicios iniciales asociados al nuevo usuario
            insertarEjerciciosIniciales(db, (int) usuarioId);
            return true;
        } else {
            return false;
        }
    }

    // Método para verificar si un usuario ya existe en la base de datos
    public boolean verificarUsuarioExiste(String usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_USUARIOS + " WHERE " + COL_USUARIO + "=?", new String[]{usuario});
        return cursor.getCount() > 0;
    }

    public boolean verificarLogin(String usuario, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_PASSWORD + " FROM " + TABLA_USUARIOS + " WHERE " + COL_USUARIO + "=?", new String[]{usuario});

        int passDB = cursor.getColumnIndex(COL_PASSWORD);
        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(passDB);
            String inputHash = hashPassword(password); // Calcula el hash de la contraseña proporcionada
            return storedHash.equals(inputHash); // Compara el hash calculado con el hash almacenado en la base de datos
        }

        cursor.close();
        return false;
    }

    // METODOS PARA LA TABLA EJERCICIOS

    // Método para insertar ejercicios iniciales para un nuevo usuario
    public void insertarEjerciciosIniciales(SQLiteDatabase db, int usuarioId){
        agregarEjercicio(db,usuarioId,new Ejercicio("Curl de Bíceps", "Ejercicio para trabajar los músculos del bíceps", R.drawable.curl_biceps, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Peso Muerto", "Ejercicio para fortalecer la espalda baja y los glúteos", R.drawable.peso_muerto, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Elevaciones Laterales", "Ejercicio para trabajar los músculos de los hombros", R.drawable.elevaciones_laterales, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Sentadilla", "Ejercicio para fortalecer las piernas y los glúteos", R.drawable.sentadilla, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Press de Banca", "Ejercicio para trabajar los músculos del pecho", R.drawable.banca, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Plancha Lateral", "Ejercicio para fortalecer el core y los músculos laterales", R.drawable.plancha_lateral, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Abdominales", "Ejercicio para trabajar los músculos abdominales", R.drawable.abdominales, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Plancha", "Ejercicio para fortalecer el core", R.drawable.plancha, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Dominadas", "Ejercicio para trabajar los músculos de la espalda", R.drawable.dominadas, "Imagen sacada de la carpeta drawable"));
        agregarEjercicio(db,usuarioId,new Ejercicio("Flexiones", "Ejercicio para fortalecer los músculos del pecho y los brazos", R.drawable.flexiones, "Imagen sacada de la carpeta drawable"));
    }

    // Método para agregar un ejercicio a la base de datos
    public void agregarEjercicio(SQLiteDatabase db, int usuarioId, Ejercicio ejercicio) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMN_USER_ID, usuarioId);
        valores.put(COLUMN_NOMBRE, ejercicio.getNombre());
        valores.put(COLUMN_DESCRIPCION, ejercicio.getDescripcion());
        valores.put(COLUMN_IMAGEN, ejercicio.getImagen());
        valores.put(COLUMN_IMAGEN_URI, ejercicio.getImagenUri());
        valores.put(COLUMN_VALORACION, ejercicio.getValoracion());

        db.insert(TABLA_EJERCICIOS, null, valores);
    }

    // Método para borrar un ejercicio de la base de datos
    public void borrarEjercicio(int idEjercicio) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Eliminar la fila correspondiente al ejercicio con el ID proporcionado
            db.delete(TABLA_EJERCICIOS, COLUMN_ID + "=?", new String[]{String.valueOf(idEjercicio)});
        } catch (Exception e) {
            Log.e("BaseDatos", "Error al borrar el ejercicio con ID " + idEjercicio + ": " + e.getMessage());
        } finally {
            // Cerrar la base de datos después de realizar la operación
            db.close();
        }
    }


    // Método para obtener todos los ejercicios asociados a un usuario
    public ArrayList<Ejercicio> obtenerTodosEjercicios(int userId) {
        ArrayList<Ejercicio> listaEjercicios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_EJERCICIOS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        int nombreIndex = cursor.getColumnIndex(COLUMN_NOMBRE);
        int descripcionIndex = cursor.getColumnIndex(COLUMN_DESCRIPCION);
        int imagenIndex = cursor.getColumnIndex(COLUMN_IMAGEN);
        int imagenUriIndex = cursor.getColumnIndex(COLUMN_IMAGEN_URI);
        int valoracionIndex = cursor.getColumnIndex(COLUMN_VALORACION);
        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(nombreIndex);
                String descripcion = cursor.getString(descripcionIndex);
                int imagen = cursor.getInt(imagenIndex);
                String imagenUri = cursor.getString(imagenUriIndex);
                float valoracion = cursor.getFloat(valoracionIndex);
                Ejercicio ejercicio = new Ejercicio(nombre, descripcion, imagen,imagenUri, valoracion);
                listaEjercicios.add(ejercicio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaEjercicios;
    }


    // Método para obtener el ID de un usuario dado su nombre de usuario
    public int obtenerIdUsuario(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        int usuarioId = -1; // Valor por defecto si no se encuentra el usuario

        Cursor cursor = db.query(TABLA_USUARIOS, new String[]{COL_ID}, COL_USUARIO + "=?", new String[]{usuario}, null, null, null);
        int id = cursor.getColumnIndex(COL_ID);
        if (cursor.moveToFirst()) {
            usuarioId = cursor.getInt(id);
        }

        cursor.close();
        return usuarioId;
    }

    // Método para obtener el ID de un ejercicio dado el nombre del usuario y el nombre del ejercicio
    public int obtenerIdEjercicioPorUsuario(String usuario, String nombreEjercicio) {
        SQLiteDatabase db = this.getReadableDatabase();
        int usuarioId = obtenerIdUsuario(usuario);
        int idEjercicio = -1;

        String selectQuery = "SELECT " + COLUMN_ID + " FROM " + TABLA_EJERCICIOS +
                " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_NOMBRE + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(usuarioId), nombreEjercicio});

        int id = cursor.getColumnIndex(COLUMN_ID);

        // Verificar si se encontró el ejercicio
        if (cursor.moveToFirst()) {
            idEjercicio = cursor.getInt(id);
        }

        cursor.close();
        return idEjercicio;
    }


    // Método para actualizar la valoración de un ejercicio en la base de datos
    public void actualizarValoracionEjercicio(int ejercicioId,float nuevaValoracion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("valoracion", nuevaValoracion);

        // Actualizar la fila correspondiente al ejercicioId
        db.update("Ejercicios", valores, "id = ?", new String[]{String.valueOf(ejercicioId)});
    }

    // Método para cifrar la contraseña usando SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                stringBuilder.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
