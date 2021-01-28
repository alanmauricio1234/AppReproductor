package mx.edu.uacm.reproductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mx.edu.uacm.reproductor.fragmentos.ListaFragment;
import mx.edu.uacm.reproductor.fragmentos.ReproductorFragment;
import mx.edu.uacm.reproductor.modelo.Cancion;
import mx.edu.uacm.reproductor.modelo.ListaCanciones;
import mx.edu.uacm.reproductor.modelo.ListaCancionesImp;
import mx.edu.uacm.reproductor.servicio.MyService;
import mx.edu.uacm.reproductor.util.Fabrica;
import mx.edu.uacm.reproductor.util.MiAdaptador;

public class MainActivity extends AppCompatActivity {
    /**
     * Se crea la lista de permisos que ocupará la aplicación
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSIONS = 12345;
    private static final int PERMISSIONS_COUNT = 1;
    public static ListaCanciones LISTA;
    private BottomNavigationView btnNavView;
    private FrameLayout frameLayout;
    private ReproductorFragment fragment;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ListaCanciones listaCanciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Se comprueban que el usuario de permisos de escritura en el dispositivo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS,  REQUEST_PERMISSIONS);
            }
        }
        btnNavView = findViewById(R.id.btnNavigation);
        //Se agrega al inicio el fragmento del reproductor
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = Fabrica.getReproductorFragmento();
        ft.replace(R.id.container, fragment);
        ft.commit();
        btnNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Se comprueba qué item selecciono el usuario para cambiar el fragmento
                if (menuItem.getItemId() == R.id.itemLista) {
                    ListaFragment fragment = Fabrica.getListaFragmento();
                    fragment.setListaCanciones(LISTA);
                    mostrarFragmentoSeleccionado(fragment);
                } else if (menuItem.getItemId() == R.id.itemReproductor) {

                    mostrarFragmentoSeleccionado(Fabrica.getReproductorFragmento());

                } else if (menuItem.getItemId() == R.id.itemRecomendaciones) {
                    mostrarFragmentoSeleccionado(Fabrica.getRecomendacionFragmento());
                }


                return true;
            }
        });
    }

    /**
     * Método que muestra el fragmento que selecciono el usuario
     * @param fragment
     */
    private void mostrarFragmentoSeleccionado(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    /**
     * El método que pide acceso a los archivos del dispositivo
     * @return boolean
     */
    @SuppressLint("NewApi")
    private boolean permisosDenegados() {
        boolean band = false;
        for (int i = 0; i < PERMISSIONS_COUNT; i++) {
            if ( checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED ) {
                band = true;
            }
        }
        return band;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //En caso de que los permisos sean denegados se vuelve a construir la app
        if (permisosDenegados()) {
            ((ActivityManager)(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            recreate();
        } else {
            onResume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && permisosDenegados()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
        //Se agrega la lista de todas as canciones del dispositivo al fragmento de reproducción
        LISTA = obtenerCanciones(this);
        fragment.setListaCanciones(LISTA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Detiene el servicio
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        stopService(intent);
    }

    /**
     * El método realiza una consulta a la base de datos del dispositivo para encontrar todos los archivos de
     * audio del dispositivo.
     * @param context
     * @return
     */
    private ListaCanciones obtenerCanciones(final Context context) {
        final ListaCanciones canciones = new ListaCancionesImp();
        canciones.setNombre("Todas");
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //Obtenemos la URI para el contenido externo del dispositivo
        //Encontrar los datos que requerimos
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ARTIST, MediaStore.Audio.AudioColumns.DURATION};
        //Se realiza la consulta
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext() ) {
                Cancion cancion = new Cancion();
                String ruta = cursor.getString(0);
                String titulo = cursor.getString(1);
                String artista = cursor.getString(2);
                Long duracion = Long.valueOf(cursor.getLong(3));
                cancion.setArtista(artista);
                cancion.setTitulo(titulo);
                cancion.setRuta(ruta);
                cancion.setDuracion(duracion);
                canciones.agregar(cancion);
            }
        }
        return canciones;
    }
}