package mx.edu.uacm.reproductor.modelo;


public interface ListaCanciones {
    public boolean agregar(Cancion cancion);
    public Cancion obtenerCancion(int posicion);
    public void setNombre(String nombre);
    public String getNombre();
    public void limpiar();
    public int tamanio();
    public boolean contiene(Cancion cancion);
}
