package main.java.com.rodasdev.sport.life.model;

public class TipoDeporte {
    private int idTipoDeporte;
    private String nombreDeporte;

    public TipoDeporte() {
    }

    public TipoDeporte(int idTipoDeporte, String nombreDeporte) {
        this.idTipoDeporte = idTipoDeporte;
        this.nombreDeporte = nombreDeporte;
    }

    public TipoDeporte(String nombreDeporte) {
        this.nombreDeporte = nombreDeporte;
    }

    public int getIdTipoDeporte() {
        return idTipoDeporte;
    }

    public void setIdTipoDeporte(int idTipoDeporte) {
        this.idTipoDeporte = idTipoDeporte;
    }

    public String getNombreDeporte() {
        return nombreDeporte;
    }

    public void setNombreDeporte(String nombreDeporte) {
        this.nombreDeporte = nombreDeporte;
    }

    @Override
    public String toString() {
        return nombreDeporte;
    }
}

