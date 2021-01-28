package mx.edu.uacm.reproductor.util;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import mx.edu.uacm.reproductor.R;
import mx.edu.uacm.reproductor.fragmentos.ReproductorFragment;
import mx.edu.uacm.reproductor.modelo.Cancion;
import mx.edu.uacm.reproductor.modelo.ListaCanciones;
import mx.edu.uacm.reproductor.servicio.MyService;
import mx.edu.uacm.reproductor.viewmodel.InfoViewModel;

public class MiAdaptador extends RecyclerView.Adapter<MiAdaptador.MyViewHolder> {
    private ListaCanciones listaCanciones;
    private InfoViewModel infoViewModel;
    private ReproductorFragment reproductorFragment = Fabrica.getReproductorFragmento();

    public MiAdaptador(ListaCanciones listaCanciones) {
        this.listaCanciones = listaCanciones;
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from( parent.getContext() )
                .inflate(R.layout.my_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.lblNombre.setText(listaCanciones.obtenerCancion(position).getTitulo());
        holder.lblArtista.setText(listaCanciones.obtenerCancion(position).getArtista());

    }

    @Override
    public int getItemCount() {
        return listaCanciones.tamanio();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblNombre, lblArtista;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lblArtista = itemView.findViewById(R.id.lblArtista);
            lblNombre = itemView.findViewById(R.id.lblnombre);
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
                    infoViewModel.setDuracion(cancion.getDuracion().intValue());
                    infoViewModel.setTitulo(cancion.getTitulo());
                    infoViewModel.setArtista(cancion.getArtista());
                    v.getContext().startService(intent);
                }
            });

        }
    }
}
