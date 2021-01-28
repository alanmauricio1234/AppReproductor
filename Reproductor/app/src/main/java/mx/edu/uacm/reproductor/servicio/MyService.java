package mx.edu.uacm.reproductor.servicio;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import mx.edu.uacm.reproductor.fragmentos.ReproductorFragment;
import mx.edu.uacm.reproductor.modelo.ListaCanciones;
import mx.edu.uacm.reproductor.util.Fabrica;

public class MyService extends Service implements MediaPlayer.OnPreparedListener  {
    private ListaCanciones listaCanciones;
    private MediaPlayer mediaPlayer;
    private ReproductorFragment rf;

    //private int indice;
    public MyService() {
        //Obtiene las instancias para utilizarlas
        mediaPlayer = Fabrica.getMediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        rf = Fabrica.getReproductorFragmento();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("servicio:", "Servicio iniciado");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Prepara los recursos para su posterior reproducción
     * @param ruta
     */
    private void preparar(String ruta) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(ruta);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int resultado = super.onStartCommand(intent, flags, startId);
        //Se realiza la acción que contiene el intent
        if (intent.getAction().equals("PLAY")) {
            String ruta = intent.getStringExtra("ruta");
            preparar(ruta);

        } else if (intent.getAction().equals("PAUSA")) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Log.d("servicio:", "reproductor en pausa");
            }
        } else if (intent.getAction().equals("SIGUIENTE")) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            String ruta = intent.getStringExtra("ruta");
            preparar(ruta);
        } else if (intent.getAction().equals("ANTERIOR")) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            String ruta = intent.getStringExtra("ruta");
            preparar(ruta);
        } else if (intent.getAction().equals("RESUME")) {
            mediaPlayer.start();
            rf.notificar();
        }
        return resultado;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();//Comienza la reprducción
        rf.getSeekBar().setMax(mediaPlayer.getDuration());
        rf.getInfoViewModel().setDuracion(mediaPlayer.getDuration());
        rf.notificar();//Notifica al fragmento reproductor para que comience el proceso
    }


}
