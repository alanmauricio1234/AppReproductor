package mx.edu.uacm.reproductor.fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.edu.uacm.reproductor.R;
import mx.edu.uacm.reproductor.modelo.ListaCanciones;
import mx.edu.uacm.reproductor.modelo.Reproductor;
import mx.edu.uacm.reproductor.util.MiAdaptador;
import mx.edu.uacm.reproductor.viewmodel.InfoViewModel;


public class ListaFragment extends Fragment {
    private ListaCanciones listaCanciones;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MiAdaptador miAdaptador;
    private InfoViewModel infoViewModel;


    public ListaFragment() {
    }

    public void setListaCanciones(ListaCanciones listaCanciones) {
        this.listaCanciones = listaCanciones;
    }

    public ListaCanciones getListaCanciones() {
        return listaCanciones;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista, container, false);
        recyclerView = v.findViewById(R.id.rcLista);
        layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);
        miAdaptador = new MiAdaptador(listaCanciones);
        recyclerView.setAdapter(miAdaptador);
        infoViewModel = new ViewModelProvider(requireActivity()).get(InfoViewModel.class);
        miAdaptador.setInfoViewModel(infoViewModel);
        return v;
    }
}