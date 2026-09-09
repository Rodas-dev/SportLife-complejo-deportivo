package main.java.com.rodasdev.sport.life.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {

    private int idReserva;
    private int idUsuario;
    private String idCodigoInstalacion;
    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private double montoTotal;
    private String estado;

    public Reserva() {
    }

    public Reserva(int idReserva, int idUsuario, String idCodigoInstalacion,
                    LocalDate fechaReserva, LocalTime horaInicio, LocalTime horaFin,
                    double montoTotal, String estado) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idCodigoInstalacion = idCodigoInstalacion;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.montoTotal = montoTotal;
        this.estado = estado;
    }

    public Reserva(int idUsuario, String idCodigoInstalacion, LocalDate fechaReserva,
                    LocalTime horaInicio, LocalTime horaFin, double montoTotal, String estado) {
        this.idUsuario = idUsuario;
        this.idCodigoInstalacion = idCodigoInstalacion;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.montoTotal = montoTotal;
        this.estado = estado;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdCodigoInstalacion() {
        return idCodigoInstalacion;
    }

    public void setIdCodigoInstalacion(String idCodigoInstalacion) {
        this.idCodigoInstalacion = idCodigoInstalacion;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", idCodigoInstalacion='" + idCodigoInstalacion + '\'' +
                ", fechaReserva=" + fechaReserva +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", montoTotal=" + montoTotal +
                ", estado='" + estado + '\'' +
                '}';
    }
}