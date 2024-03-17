package com.example.proyecto_das;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.ViewHolder> {
    private final ArrayList<Ejercicio> ejercicios;
    private final Context context;
    private final String usuario;
    private BaseDatos db;

    // Constructor del adaptador
    public EjercicioAdapter(Context context, ArrayList<Ejercicio> ejercicios, String usuario) {
        this.context = context;
        this.ejercicios = ejercicios;
        this.usuario = usuario;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagen;
        public TextView nombreEjer;
        public TextView descripcionEjer;
        public RatingBar valoracionEjer;
        public ImageView botonPapelera;

        public ViewHolder(View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imageView);
            nombreEjer = itemView.findViewById(R.id.textViewNombre);
            descripcionEjer = itemView.findViewById(R.id.textViewDescripcion);
            valoracionEjer = itemView.findViewById(R.id.valoracion);
            botonPapelera = itemView.findViewById(R.id.botonPapelera);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ejercicio ejercicio = ejercicios.get(position);

        // Cargar la imagen del ejercicio en ImageView
        if (ejercicio.getImagen() == 0) {
            holder.imagen.setImageURI(Uri.parse(ejercicio.getImagenUri()));
        } else {
            // Si el identificador de la imagen no es 0, cargar la imagen desde la carpeta drawable
            holder.imagen.setImageResource(ejercicio.getImagen());
        }

        holder.nombreEjer.setText(ejercicio.getNombre());
        holder.descripcionEjer.setText(ejercicio.getDescripcion());

        // Obtener el ID del ejercicio para borrar
        db = new BaseDatos(context);
        int idEjercicio = db.obtenerIdEjercicioPorUsuario(usuario, ejercicio.getNombre());

        // Establecer el listener de clic para el botón de papelera
        holder.botonPapelera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.dlg_borrarT);
                builder.setMessage(R.string.dlg_borrarQ);
                builder.setPositiveButton(R.string.dlg_Aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Borrar el ejercicio de la base de datos y actualizar la lista
                        db.borrarEjercicio(idEjercicio);
                        ejercicios.remove(ejercicio);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(R.string.dlg_Cancelar, null);
                builder.create().show();
            }
        });

        // Obtener la orientación actual de la pantalla
        int orientation = holder.itemView.getResources().getConfiguration().orientation;

        // Mostrar la valoración si la orientacion es en vertical
        if (orientation == Configuration.ORIENTATION_PORTRAIT ) {
            holder.valoracionEjer.setVisibility(View.VISIBLE);
            holder.valoracionEjer.setRating(ejercicio.getValoracion());

            // Configurar un listener para detectar cambios en la valoración
            holder.valoracionEjer.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    // Actualizar la valoración en el objeto Ejercicio
                    ejercicio.setValoracion(rating);

                    // Guardar la valoración actualizada en la base de datos
                    db.actualizarValoracionEjercicio(idEjercicio, rating);
                }
            });
        } else {
            // Si la orientacion es horizontal ocultar valoracion
            holder.valoracionEjer.setVisibility(View.GONE);
        }

    }

    // Devuelve la cantidad de elementos en la lista de ejercicios
    @Override
    public int getItemCount() {
        return ejercicios.size();
    }
}
