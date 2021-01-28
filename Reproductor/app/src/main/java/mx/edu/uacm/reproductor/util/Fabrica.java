package mx.edu.uacm.reproductor.util;

import android.media.MediaPlayer;

import mx.edu.uacm.reproductor.fragmentos.ListaFragment;
import mx.edu.uacm.reproductor.fragmentos.RecomendacionFragment;
import mx.edu.uacm.reproductor.fragmentos.ReproductorFragment;

/**
 * Esta clase funciona como fabrica de objetos. Para crear una sola instancia.
 */
public class Fabrica {
    private static ListaFragment LISTA_FRAGMENTO = null;
    private static ReproductorFragment REPRODUCTOR_FRAGMENTO = null;
    private static RecomendacionFragment RECOMENDACION_FRAGMENTO = null;
    private static MediaPlayer MEDIA_PLAYER = null;

    public static ListaFragment getListaFragmento() {
        if (LISTA_FRAGMENTO == null) {
            LISTA_FRAGMENTO = new ListaFragment();
        }

        return LISTA_FRAGMENTO;
    }

    public static RecomendacionFragment getRecomendacionFragmento() {
        if ( RECOMENDACION_FRAGMENTO == null) {
            RECOMENDACION_FRAGMENTO = new RecomendacionFragment();
        }

        return RECOMENDACION_FRAGMENTO;
    }

    public static ReproductorFragment getReproductorFragmento() {
        if (REPRODUCTOR_FRAGMENTO == null) {
            REPRODUCTOR_FRAGMENTO = new ReproductorFragment();

        }

        return REPRODUCTOR_FRAGMENTO;
    }

    public static MediaPlayer getMediaPlayer() {
        if (MEDIA_PLAYER == null) {
            MEDIA_PLAYER = new MediaPlayer();
        }

        return MEDIA_PLAYER;
    }

}
