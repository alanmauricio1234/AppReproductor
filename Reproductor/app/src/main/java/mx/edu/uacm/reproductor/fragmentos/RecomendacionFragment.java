package mx.edu.uacm.reproductor.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mx.edu.uacm.reproductor.R;
import mx.edu.uacm.reproductor.modelo.Cancion;
import mx.edu.uacm.reproductor.modelo.ListaCanciones;
import mx.edu.uacm.reproductor.modelo.ListaCancionesImp;
import mx.edu.uacm.reproductor.util.AdaptadorFire;
import mx.edu.uacm.reproductor.util.MiAdaptador;
import mx.edu.uacm.reproductor.viewmodel.InfoViewModel;


public class RecomendacionFragment extends Fragment {
    private ListaCanciones listaCanciones;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdaptadorFire miAdaptador;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private InfoViewModel infoViewModel;

    public RecomendacionFragment() {
    }

    public ListaCanciones getListaCanciones() {
        return listaCanciones;
    }

    public void setListaCanciones(ListaCanciones listaCanciones) {
        this.listaCanciones = listaCanciones;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion, container, false);
        miAdaptador = new AdaptadorFire(v.getContext());
        obtenerDatosFirebase();
        recyclerView = v.findViewById(R.id.rcListaFire);
        layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);

        miAdaptador.setListaCanciones(listaCanciones);
        recyclerView.setAdapter(miAdaptador);
        infoViewModel = new ViewModelProvider(requireActivity()).get(InfoViewModel.class);
        miAdaptador.setInfoViewModel(infoViewModel);
        return v;
    }

    /**
     * En este método se obtiene la lista de canciones de recomendación que se encuentran almacenadas
     * en la base de datos en tiempo real de firebase.
     */
    private void obtenerDatosFirebase() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("recomendaciones");
        listaCanciones = new ListaCancionesImp();
        listaCanciones.setNombre("Recomendaciones");


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(getContext(), "Se agrego una nueva cancion", Toast.LENGTH_LONG).show();
                Cancion c = dataSnapshot.getValue(Cancion.class);
                if (!listaCanciones.contiene(c)) {
                    listaCanciones.agregar(c);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}