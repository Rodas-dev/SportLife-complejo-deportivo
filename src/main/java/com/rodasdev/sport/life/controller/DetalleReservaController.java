package main.java.com.rodasdev.sport.life.controller;

import main.java.com.rodasdev.sport.life.model.DataBaseConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DetalleReservaController {

    @FXML
    private ImageView imagenCancha;

    @FXML
    private Label etiquetaDescripcion;

    @FXML
    private Label etiquetaTipoDeporte;

    @FXML
    private Label etiquetaPrecioHora;

    @FXML
    private DatePicker selectorFecha;

    @FXML
    private ComboBox<String> comboHoraInicio;

    @FXML
    private ComboBox<String> comboHoraFin;

    @FXML
    private Label etiquetaOcupados;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Label etiquetaMontoTotal;

    @FXML
    private Button botonConfirmar;

    @FXML
    private Button botonVolver;

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private int idUsuario;
    private String nombreUsuario;
    private String codigoInstalacion;
    private double precioHora;
    private List<LocalTime[]> horariosOcupados;

    @FXML
    public void initialize() {
        List<String> horas = new ArrayList<>();
        for (int h = 8; h <= 22; h++) {
            horas.add(String.format("%02d:00", h));
        }
        comboHoraInicio.setItems(FXCollections.observableArrayList(horas));
        comboHoraFin.setItems(FXCollections.observableArrayList(horas));
        horariosOcupados = new ArrayList<>();
    }

    public void inicializarReserva(int idUsuario, String nombreUsuario, String codigoInstalacion, String descripcion,
                                    String tipoDeporte, double precioHora, String fotoUrl) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.codigoInstalacion = codigoInstalacion;
        this.precioHora = precioHora;

        etiquetaDescripcion.setText(descripcion);
        etiquetaTipoDeporte.setText(tipoDeporte);
        etiquetaPrecioHora.setText(String.format("Q%.2f / hora", precioHora));

        try {
            File archivo = new File(fotoUrl);
            if (archivo.exists()) {
                imagenCancha.setImage(new Image(archivo.toURI().toString()));
            }
        } catch (Exception e) {
        }
    }

    @FXML
    private void alCambiarFecha(ActionEvent evento) {
        LocalDate fecha = selectorFecha.getValue();
        if (fecha == null) {
            return;
        }
        cargarHorariosOcupados(fecha);
        actualizarMonto();
    }

    private void cargarHorariosOcupados(LocalDate fecha) {
        horariosOcupados.clear();
        String sql = "SELECT hora_inicio, hora_fin FROM RESERVAS "
                + "WHERE id_codigo_instalacion = ? AND fecha_reserva = ? AND estado <> 'CANCELADA'";

        try (Connection conexion = DataBaseConnection.getDataBaseConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigoInstalacion);
            sentencia.setDate(2, java.sql.Date.valueOf(fecha));

            try (ResultSet resultado = sentencia.executeQuery()) {
                StringBuilder texto = new StringBuilder("Horarios ocupados: ");
                boolean hayOcupados = false;

                while (resultado.next()) {
                    LocalTime inicio = resultado.getTime("hora_inicio").toLocalTime();
                    LocalTime fin = resultado.getTime("hora_fin").toLocalTime();
                    horariosOcupados.add(new LocalTime[]{inicio, fin});
                    texto.append(inicio.format(FORMATO_HORA)).append("-").append(fin.format(FORMATO_HORA)).append("  ");
                    hayOcupados = true;
                }

                etiquetaOcupados.setText(hayOcupados ? texto.toString() : "No hay horarios ocupados ese día.");
            }

        } catch (SQLException e) {
            etiquetaOcupados.setText("No se pudieron cargar los horarios ocupados.");
        }
    }

    @FXML
    private void alCambiarHorario(ActionEvent evento) {
        actualizarMonto();
    }

    private void actualizarMonto() {
        String horaInicioTexto = comboHoraInicio.getValue();
        String horaFinTexto = comboHoraFin.getValue();
        etiquetaMensaje.setText("");

        if (horaInicioTexto == null || horaFinTexto == null) {
            etiquetaMontoTotal.setText("Total: Q0.00");
            return;
        }

        LocalTime horaInicio = LocalTime.parse(horaInicioTexto, FORMATO_HORA);
        LocalTime horaFin = LocalTime.parse(horaFinTexto, FORMATO_HORA);

        if (!horaFin.isAfter(horaInicio)) {
            etiquetaMensaje.setText("La hora de fin debe ser posterior a la hora de inicio.");
            etiquetaMontoTotal.setText("Total: Q0.00");
            return;
        }

        long horas = java.time.Duration.between(horaInicio, horaFin).toHours();
        double montoTotal = horas * precioHora;
        etiquetaMontoTotal.setText(String.format("Total: Q%.2f", montoTotal));
    }

    private boolean seSuperponeConOcupados(LocalTime horaInicio, LocalTime horaFin) {
        for (LocalTime[] rango : horariosOcupados) {
            if (horaInicio.isBefore(rango[1]) && horaFin.isAfter(rango[0])) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void alConfirmarReserva(ActionEvent evento) {
        LocalDate fecha = selectorFecha.getValue();
        String horaInicioTexto = comboHoraInicio.getValue();
        String horaFinTexto = comboHoraFin.getValue();

        if (fecha == null || horaInicioTexto == null || horaFinTexto == null) {
            etiquetaMensaje.setText("Elige fecha y horario completos.");
            return;
        }

        LocalTime horaInicio = LocalTime.parse(horaInicioTexto, FORMATO_HORA);
        LocalTime horaFin = LocalTime.parse(horaFinTexto, FORMATO_HORA);

        if (!horaFin.isAfter(horaInicio)) {
            etiquetaMensaje.setText("La hora de fin debe ser posterior a la hora de inicio.");
            return;
        }

        if (seSuperponeConOcupados(horaInicio, horaFin)) {
            etiquetaMensaje.setText("Ese horario ya está ocupado, elige otro.");
            return;
        }

        long horas = java.time.Duration.between(horaInicio, horaFin).toHours();
        double montoTotal = horas * precioHora;

        String sql = "INSERT INTO RESERVAS (id_usuario, id_codigo_instalacion, fecha_reserva, "
                + "hora_inicio, hora_fin, monto_total, estado) VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMADA')";

        try (Connection conexion = DataBaseConnection.getDataBaseConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);
            sentencia.setString(2, codigoInstalacion);
            sentencia.setDate(3, java.sql.Date.valueOf(fecha));
            sentencia.setTime(4, java.sql.Time.valueOf(horaInicio));
            sentencia.setTime(5, java.sql.Time.valueOf(horaFin));
            sentencia.setDouble(6, montoTotal);

            sentencia.executeUpdate();

            etiquetaMensaje.setStyle("-fx-text-fill: #2e7d32;");
            etiquetaMensaje.setText("Reserva confirmada con éxito.");
            cargarHorariosOcupados(fecha);

        } catch (SQLException e) {
            etiquetaMensaje.setStyle("-fx-text-fill: #c62828;");
            etiquetaMensaje.setText("No se pudo confirmar la reserva.");
        }
    }

    @FXML
    private void alVolver(ActionEvent evento) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/main/resources/view/reservar.fxml"));
            Parent raiz = cargador.load();

            ReservarController controlador = cargador.getController();
            controlador.inicializarUsuario(idUsuario, nombreUsuario);

            Stage escenario = (Stage) ((Button) evento.getSource()).getScene().getWindow();
            escenario.setScene(new Scene(raiz));
            escenario.show();
        } catch (IOException e) {
        }
    }
}