package mx.edu.uacm.reproductor.modelo;


import android.os.Parcel;
import android.os.Parcelable;

public class Cancion implements Parcelable {
    private String titulo;
    private String artista;
    private Long duracion;
    private String ruta;

    public Cancion() {
        duracion = Long.valueOf(1);
    }

    protected Cancion(Parcel in) {
        titulo = in.readString();
        artista = in.readString();
        if (in.readByte() == 0) {
            duracion = null;
        } else {
            duracion = in.readLong();
        }
        ruta = in.readString();
    }

    public static final Creator<Cancion> CREATOR = new Creator<Cancion>() {
        @Override
        public Cancion createFromParcel(Parcel in) {
            return new Cancion(in);
        }

        @Override
        public Cancion[] newArray(int size) {
            return new Cancion[size];
        }
    };

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

    public Long getDuracion() {
        return duracion;
    }

    public void setDuracion(Long duracion) {
        this.duracion = duracion;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public static String formatoReloj(long tiempo) {
        String formato = "";
        tiempo = tiempo / 1000;
        long segundo = tiempo % 60;
        long minuto = tiempo / 60;
        String fSegundo = "" + segundo;
        String fMinuto = "" + minuto;
        if (segundo < 10) {
            fSegundo = "0" + segundo;
        }
        formato += fMinuto + ":" + fSegundo;
        return formato;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", duracion=" + duracion +
                ", ruta='" + ruta + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(artista);
        if (duracion == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(duracion);
        }
        dest.writeString(ruta);
    }
}