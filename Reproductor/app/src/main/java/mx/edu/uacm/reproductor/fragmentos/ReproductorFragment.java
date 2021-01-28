package mx.edu.uacm.reproductor.fragmentos;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import mx.edu.uacm.reproductor.R;
import mx.edu.uacm.reproductor.modelo.Cancion;
import mx.edu.uacm.reproductor.modelo.ListaCanciones;
import mx.edu.uacm.reproductor.modelo.Reproductor;
import mx.edu.uacm.reproductor.servicio.MyService;
import mx.edu.uacm.reproductor.util.Fabrica;
import mx.edu.uacm.reproductor.viewmodel.InfoViewModel;


public class ReproductorFragment extends Fragment implements Reproductor, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private Button btnDetener, btnSiguiente, btnAnterior;
    private ImageView imgPlayPausa;
    private TextView lblTitulo, lblArtista, lblTiempo, lblDuracion;
    private SeekBar seekBar;
    private ListaCanciones listaCanciones;
    private int indice = 0;
    private boolean pausado = false;
    private Handler handler = new Handler();
    private InfoViewModel infoViewModel;

    public ReproductorFragment() {
        mediaPlayer = Fabrica.getMediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public ListaCanciones getListaCanciones() {
        return listaCanciones;
    }

    public void setListaCanciones(ListaCanciones listaCanciones) {
        this.listaCanciones = listaCanciones;
    }

    public InfoViewModel getInfoViewModel() {
        return infoViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reproductor, container, false);
        btnAnterior = v.findViewById(R.id.btnAnterior);
        btnDetener = v.findViewById(R.id.btnStop);
        imgPlayPausa = v.findViewById(R.id.imgPlayPause);
        btnSiguiente = v.findViewById(R.id.btnSiguiente);
        lblTitulo = v.findViewById(R.id.lblTitulo);
        lblDuracion = v.findViewById(R.id.lblDuracion);
        lblTiempo = v.findViewById(R.id.lblTiempo);
        lblArtista = v.findViewById(R.id.lblMArtista);
        seekBar = v.findViewById(R.id.seekBar);
        infoViewModel = new ViewModelProvider(requireActivity()).get(InfoViewModel.class);

        lblArtista.setText(infoViewModel.getArtista());
        lblTitulo.setText(infoViewModel.getTitulo());
        indice = infoViewModel.getIndice();
        pausado = infoViewModel.isPausado();
        if (infoViewModel.getDuracion() >= 0) {
            seekBar.setMax(infoViewModel.getDuracion());
            lblDuracion.setText( Cancion.formatoReloj(infoViewModel.getDuracion()) );
            imgPlayPausa.setImageResource(R.drawable.ic_baseline_pause);
        }


        imgPlayPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    reproducir();
                    imgPlayPausa.setImageResource(R.drawable.ic_baseline_pause);
                    actualizarSeekBar();
                } else {
                    pausar();
                    imgPlayPausa.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    handler.removeCallbacks(actualizacion);
                }
            }
        });

        btnDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detener();
            }
        });

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siguiente();
            }
        });

        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anterior();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progreso;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progreso = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(progreso);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void notificar() {
        actualizarSeekBar();
    }

    private Runnable actualizacion = new Runnable() {
        @Override
        public void run() {
            actualizarSeekBar();
            long tiempoTrasncurrido = mediaPlayer.getCurrentPosition();
            lblTiempo.setText(Cancion.formatoReloj(tiempoTrasncurrido));
        }
    };

    private void actualizarSeekBar() {

        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(actualizacion, 1000);
        }
    }

    private void enviarIntent(String action) {
        Intent intent = new Intent(getContext(), MyService.class);
        intent.putExtra("ruta", listaCanciones.obtenerCancion(indice).getRuta());

        intent.setAction(action);
        infoViewModel.setArtista(listaCanciones.obtenerCancion(indice).getArtista());
        infoViewModel.setTitulo(listaCanciones.obtenerCancion(indice).getTitulo());
        lblTitulo.setText( infoViewModel.getTitulo() );
        lblArtista.setText( infoViewModel.getArtista() );
        this.getActivity().startService(intent);
        Long duracion = listaCanciones.obtenerCancion(indice).getDuracion();
        seekBar.setMax(duracion.intValue());
        infoViewModel.setDuracion(duracion.intValue());
        lblDuracion.setText(Cancion.formatoReloj(duracion));

    }

    private  void resume() {
        Intent intent = new Intent(getContext(), MyService.class);
        intent.setAction("RESUME");
        this.getActivity().startService(intent);
    }



    @Override
    public void reproducir() {
        if ( pausado ) {
            resume();
            return;
        }
        enviarIntent("PLAY");

    }

    @Override
    public void pausar() {
        Intent intent = new Intent(getContext(), MyService.class);
        intent.setAction("PAUSA");
        getActivity().startService(intent);
        pausado = true;
        infoViewModel.setPausado(pausado);
    }

    @Override
    public void detener() {
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(actualizacion);
            Intent intent = new Intent(getContext(), MyService.class);
            this.getActivity().stopService(intent);
            seekBar.setProgress(0);
            lblTiempo.setText("00:00");
        }
    }

    @Override
    public void siguiente() {
        indice++;
        if (indice > listaCanciones.tamanio() - 1) {
            indice = 0;
        }
        infoViewModel.setIndice(indice);
        enviarIntent("SIGUIENTE");

    }

    @Override
    public void anterior() {
        indice--;
        if (indice < 0) {
            indice = listaCanciones.tamanio() - 1;
        }
        infoViewModel.setIndice(indice);
        enviarIntent("ANTERIOR");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        siguiente();
    }
}