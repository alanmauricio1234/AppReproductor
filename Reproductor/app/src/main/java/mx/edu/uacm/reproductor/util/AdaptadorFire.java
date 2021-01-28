package mx.edu.uacm.reproductor.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import mx.edu.uacm.reproductor.R;
import mx.edu.uacm.reproductor.fragmentos.ReproductorFragment;
import mx.edu.uacm.reproductor.modelo.Cancion;
import mx.edu.uacm.reproductor.modelo.ListaCanciones;
import mx.edu.uacm.reproductor.servicio.MyService;
import mx.edu.uacm.reproductor.viewmodel.InfoViewModel;

public class AdaptadorFire extends RecyclerView.Adapter<AdaptadorFire.MyViewHolder> {
    private Context context;
    private ListaCanciones listaCanciones;
    private InfoViewModel infoViewModel;
    private ReproductorFragment reproductorFragment = Fabrica.getReproductorFragmento();

    public AdaptadorFire(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

    public void setInfoViewModel(InfoViewModel infoViewModel) {
        this.infoViewModel = infoViewModel;
    }

    @NonNull
    @Override
    public AdaptadorFire.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from( parent.getContext() )
                .inflate(R.layout.item_fire, parent, false); //Inflamos la vista
        MyViewHolder vh = new MyViewHolder(v); // Se crea el ViewHolder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorFire.MyViewHolder holder, final int position) {
        holder.lblNombre.setText(listaCanciones.obtenerCancion(position).getTitulo());
        holder.lblArtista.setText(listaCanciones.obtenerCancion(position).getArtista());
        //Se realiza la acción del botón
        holder.btnDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ruta = listaCanciones.obtenerCancion(position).getRuta();
                String nombre = listaCanciones.obtenerCancion(position).getTitulo();
                descargar(ruta, nombre);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCanciones.tamanio();
    }

    /**
     * En este métoodo se realiza la descarga del archivo
     * @param url
     * @param nombreArchivo
     */
    private void descargar(String url, String nombreArchivo) {
        DownloadManager.Request request = new DownloadManager.Request( Uri.parse(url) );
        request.setTitle(nombreArchivo);
        request.setDescription("Descargando: " + nombreArchivo);
        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreArchivo);
        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblNombre, lblArtista;
        public Button btnDescargar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lblArtista = itemView.findViewById(R.id.lblArtista_item_fire);
            lblNombre = itemView.findViewById(R.id.lblTitulo_item_fire);
            btnDescargar = itemView.findViewById(R.id.btnDescargar_item_fire);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Se verifica que la lista del adaptador sea la misma que la del fragmento reproductor
                    if ( !reproductorFragment.getListaCanciones().getNombre().equals( listaCanciones.getNombre() ) ) {
                        reproductorFragment.setListaCanciones(listaCanciones);
                    }
                    int posicion = getAdapterPosition();
                    Cancion cancion = listaCanciones.obtenerCancion( posicion );
                    Intent intent = new Intent(v.getContext(), MyService.class);
                    intent.putExtra("ruta", cancion.getRuta());
                    intent.setAction("PLAY");
                    infoViewModel.setIndice(posicion);
                    infoViewModel.setTitulo(cancion.getTitulo());
                    infoViewModel.setArtista(cancion.getArtista());
                    v.getContext().startService(intent);
                }
            });
        }
    }
}
