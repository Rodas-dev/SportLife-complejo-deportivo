package main.java.com.rodasdev.sport.life.controller;

import main.java.com.rodasdev.sport.life.model.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MisReservasController {

    @FXML
    private FlowPane contenedorReservas;

    @FXML
    private Button botonVolver;

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int idUsuario;
    private String nombreUsuario;

    public void inicializarUsuario(int idUsuario, String nombreUsuario) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        cargarReservas();
    }

    private void cargarReservas() {
        contenedorReservas.getChildren().clear();
        String sql = "SELECT r.id_reserva, r.fecha_reserva, r.hora_inicio, r.hora_fin, r.monto_total, r.estado, "
                + "i.descripcion, i.foto_url, t.nombre_deporte "
                + "FROM RESERVAS r "
                + "JOIN INSTALACIONES_DEPORTIVAS i ON r.id_codigo_instalacion = i.id_codigo_instalacion "
                + "JOIN TIPO_DEPORTE t ON i.id_tipo_deporte = t.id_tipo_deporte "
                + "WHERE r.id_usuario = ? "
                + "ORDER BY r.fecha_reserva DESC, r.hora_inicio DESC";

        try (Connection conexion = DataBaseConnection.getDataBaseConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    contenedorReservas.getChildren().add(crearTarjetaReserva(
                            resultado.getInt("id_reserva"),
                            resultado.getDate("fecha_reserva").toLocalDate(),
                            resultado.getTime("hora_inicio").toLocalTime(),
                            resultado.getTime("hora_fin").toLocalTime(),
                            resultado.getDouble("monto_total"),
                            resultado.getString("estado"),
                            resultado.getString("descripcion"),
                            resultado.getString("nombre_deporte"),
                            resultado.getString("foto_url")
                    ));
                }
            }

        } catch (SQLException e) {
        }
    }

    private VBox crearTarjetaReserva(int idReserva, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                                      double montoTotal, String estado, String descripcion,
                                      String tipoDeporte, String fotoUrl) {

        ImageView imagen = new ImageView();
        imagen.setFitWidth(240);
        imagen.setFitHeight(140);
        imagen.setPreserveRatio(true);
        cargarImagen(imagen, fotoUrl);

        Label etiquetaDescripcion = new Label(descripcion + " · " + tipoDeporte);
        etiquetaDescripcion.getStyleClass().add("titulo-cancha");
        etiquetaDescripcion.setWrapText(true);

        Label etiquetaFechaHora = new Label(fecha.format(FORMATO_FECHA) + "   "
                + horaInicio.format(FORMATO_HORA) + " - " + horaFin.format(FORMATO_HORA));
        etiquetaFechaHora.getStyleClass().add("dato-cancha");

        Label etiquetaMonto = new Label(String.format("Q%.2f", montoTotal));
        etiquetaMonto.getStyleClass().add("precio-cancha");

        Label etiquetaEstado = new Label(estado);
        etiquetaEstado.getStyleClass().add("CONFIRMADA".equals(estado) ? "estado-confirmada" : "estado-cancelada");

        VBox tarjeta = new VBox(8, imagen, etiquetaDescripcion, etiquetaFechaHora, etiquetaMonto, etiquetaEstado);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("tarjeta-cancha");
        tarjeta.setPrefWidth(260);

        boolean esFutura = LocalDateTime.of(fecha, horaInicio).isAfter(LocalDateTime.now());
        if ("CONFIRMADA".equals(estado) && esFutura) {
            Button botonCancelar = new Button("Cancelar");
            botonCancelar.getStyleClass().add("boton-cancelar");
            botonCancelar.setMaxWidth(Double.MAX_VALUE);
            botonCancelar.setOnAction(evento -> cancelarReserva(idReserva));
            tarjeta.getChildren().add(botonCancelar);
        }

        return tarjeta;
    }

    private void cancelarReserva(int idReserva) {
        String sql = "UPDATE RESERVAS SET estado = 'CANCELADA' WHERE id_reserva = ?";

        try (Connection conexion = DataBaseConnection.getDataBaseConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idReserva);
            sentencia.executeUpdate();
            cargarReservas();

        } catch (SQLException e) {
        }
    }

    private void cargarImagen(ImageView imagen, String fotoUrl) {
        String nombreArchivo = (fotoUrl == null || fotoUrl.isBlank()) ? "sin-foto.jpg" : fotoUrl;
        InputStream recurso = getClass().getResourceAsStream("/main/resources/imagenes/" + nombreArchivo);
        if (recurso == null) {
            recurso = getClass().getResourceAsStream("/main/resources/imagenes/sin-foto.jpg");
        }
        if (recurso != null) {
            imagen.setImage(new Image(recurso));
        }
    }

    @FXML
    private void alVolver(ActionEvent evento) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/main/resources/view/cliente.fxml"));
            Parent raiz = cargador.load();

            ClienteController controlador = cargador.getController();
            controlador.inicializarUsuario(idUsuario, nombreUsuario);

            cambiarEscena(evento, raiz);
        } catch (Exception e) {
        }
    }

    private void cambiarEscena(ActionEvent evento, Parent raiz) {
        Stage escenario = (Stage) ((Button) evento.getSource()).getScene().getWindow();
        Scene escena = new Scene(raiz, 950, 850);
        escenario.setScene(escena);
        escenario.setMinWidth(950);
        escenario.setMinHeight(850);
        escenario.centerOnScreen();
        escenario.show();
    }
}