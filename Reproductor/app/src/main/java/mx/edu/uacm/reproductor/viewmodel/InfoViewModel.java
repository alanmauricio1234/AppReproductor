package mx.edu.uacm.reproductor.viewmodel;

import androidx.lifecycle.ViewModel;


public class InfoViewModel extends ViewModel {
    private String titulo;
    private String artista;
    private int indice;
    private boolean pausado = false;
    private int duracion;

    public InfoViewModel() {
        titulo = "Nombre";
        artista = "Artista";
        indice = 0;
        duracion = -1;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }


    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public boolean isPausado() {
        return pausado;
    }

    public void setPausado(boolean pausado) {
        this.pausado = pausado;
    }
}
