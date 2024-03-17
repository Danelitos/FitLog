package com.example.proyecto_das;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogoActivity extends DialogFragment {

    // Interfaces para los listeners del diálogo
    protected ListenerdelDialogoMain miListenerMainActivity;
    protected ListenerdelDialogoAnadirEjercicio miListenerAnadirEjercicioActivity;

    // Constantes para identificar el tipo de diálogo
    private static final int DIALOGO_CERRAR_SESION = 1;
    private static final int DIALOGO_ANADIR_EJERCICIO = 2;
    private int tipoDialogo;

    public interface ListenerdelDialogoMain {
        void alPulsarCerrarSesion();
        void alPulsarAceptarCerrarSesion();
    }
    public interface ListenerdelDialogoAnadirEjercicio {
        void alPulsarAnadir();
        void alPulsarAceptarAnadirEjercicio();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Obtener el tipo de diálogo del argumento pasado
        Bundle args = getArguments();
        if (args != null) {
            tipoDialogo = args.getInt("tipoDialogo");
        }

        // Construir el diálogo según el tipo
        if (tipoDialogo == DIALOGO_CERRAR_SESION) {
            builder.setTitle(R.string.dlg_cerrarSesionT);
            builder.setMessage(R.string.dlg_cerrarSesionQ);
            builder.setPositiveButton(R.string.dlg_Aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (miListenerMainActivity != null) {
                        miListenerMainActivity.alPulsarAceptarCerrarSesion();
                    }
                }
            });
            builder.setNegativeButton(R.string.dlg_Cancelar, null);
        }
        else if (tipoDialogo == DIALOGO_ANADIR_EJERCICIO){
            builder.setTitle(R.string.dlg_anadirT);
            builder.setMessage(R.string.dlg_anadirQ);
            builder.setPositiveButton(R.string.dlg_Aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (miListenerAnadirEjercicioActivity != null) {
                        miListenerAnadirEjercicioActivity.alPulsarAceptarAnadirEjercicio();
                    }
                }
            });
            builder.setNegativeButton(R.string.dlg_Cancelar, null);
        }

        return builder.create();
    }
}
