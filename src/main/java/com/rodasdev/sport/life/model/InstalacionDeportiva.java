package main.java.com.rodasdev.sport.life.model;

public class InstalacionDeportiva {

    private String idCodigoInstalacion;
    private int idTipoDeporte;
    private int idAdmin;
    private String descripcion;
    private boolean esTechada;
    private double precioHora;
    private String fotoUrl;
    private boolean habilitada;

    public InstalacionDeportiva() {
    }

    public InstalacionDeportiva(String idCodigoInstalacion, int idTipoDeporte, int idAdmin,
                                 String descripcion, boolean esTechada, double precioHora,
                                 String fotoUrl, boolean habilitada) {
        this.idCodigoInstalacion = idCodigoInstalacion;
        this.idTipoDeporte = idTipoDeporte;
        this.idAdmin = idAdmin;
        this.descripcion = descripcion;
        this.esTechada = esTechada;
        this.precioHora = precioHora;
        this.fotoUrl = fotoUrl;
        this.habilitada = habilitada;
    }

    public String getIdCodigoInstalacion() {
        return idCodigoInstalacion;
    }

    public void setIdCodigoInstalacion(String idCodigoInstalacion) {
        this.idCodigoInstalacion = idCodigoInstalacion;
    }

    public int getIdTipoDeporte() {
        return idTipoDeporte;
    }

    public void setIdTipoDeporte(int idTipoDeporte) {
        this.idTipoDeporte = idTipoDeporte;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEsTechada() {
        return esTechada;
    }

    public void setEsTechada(boolean esTechada) {
        this.esTechada = esTechada;
    }

    public double getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(double precioHora) {
        this.precioHora = precioHora;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }

    @Override
    public String toString() {
        return "InstalacionDeportiva{" +
                "idCodigoInstalacion='" + idCodigoInstalacion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precioHora=" + precioHora +
                ", habilitada=" + habilitada +
                '}';
    }
}