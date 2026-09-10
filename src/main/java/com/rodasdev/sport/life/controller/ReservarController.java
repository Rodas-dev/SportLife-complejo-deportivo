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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservarController {

    @FXML
    private FlowPane contenedorCanchas;

    @FXML
    private Button botonVolver;

    private int idUsuario;
    private String nombreUsuario;

    public void inicializarUsuario(int idUsuario, String nombreUsuario) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        cargarCanchas();
    }

    private void cargarCanchas() {
        contenedorCanchas.getChildren().clear();
        String sql = "SELECT i.id_codigo_instalacion, i.descripcion, i.es_techada, i.precio_hora, "
                + "i.foto_url, t.nombre_deporte "
                + "FROM INSTALACIONES_DEPORTIVAS i "
                + "JOIN TIPO_DEPORTE t ON i.id_tipo_deporte = t.id_tipo_deporte "
                + "WHERE i.habilitada = TRUE";

        try (Connection conexion = DataBaseConnection.getDataBaseConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                contenedorCanchas.getChildren().add(crearTarjetaCancha(
                        resultado.getString("id_codigo_instalacion"),
                        resultado.getString("descripcion"),
                        resultado.getString("nombre_deporte"),
                        resultado.getBoolean("es_techada"),
                        resultado.getDouble("precio_hora"),
                        resultado.getString("foto_url")
                ));
            }

        } catch (SQLException e) {
        }
    }

    private VBox crearTarjetaCancha(String codigo, String descripcion, String tipoDeporte,
                                     boolean esTechada, double precioHora, String fotoUrl) {

        ImageView imagen = new ImageView();
        imagen.setFitWidth(240);
        imagen.setFitHeight(140);
        imagen.setPreserveRatio(true);
        cargarImagen(imagen, fotoUrl);

        Label etiquetaDescripcion = new Label(descripcion);
        etiquetaDescripcion.getStyleClass().add("titulo-cancha");
        etiquetaDescripcion.setWrapText(true);

        Label etiquetaTipo = new Label(tipoDeporte + " · " + (esTechada ? "Techada" : "Aire libre"));
        etiquetaTipo.getStyleClass().add("dato-cancha");

        Label etiquetaPrecio = new Label(String.format("Q%.2f / hora", precioHora));
        etiquetaPrecio.getStyleClass().add("precio-cancha");

        Button botonReservar = new Button("Reservar");
        botonReservar.getStyleClass().add("boton-primario");
        botonReservar.setMaxWidth(Double.MAX_VALUE);
        botonReservar.setOnAction(evento -> irADetalleReserva(evento, codigo, descripcion, tipoDeporte, precioHora, fotoUrl));

        VBox tarjeta = new VBox(8, imagen, etiquetaDescripcion, etiquetaTipo, etiquetaPrecio, botonReservar);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("tarjeta-cancha");
        tarjeta.setPrefWidth(260);

        return tarjeta;
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

    private void irADetalleReserva(ActionEvent evento, String codigo, String descripcion,
                                    String tipoDeporte, double precioHora, String fotoUrl) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/main/resources/view/detalle-reserva.fxml"));
            Parent raiz = cargador.load();

            DetalleReservaController controlador = cargador.getController();
            controlador.inicializarReserva(idUsuario, nombreUsuario, codigo, descripcion, tipoDeporte, precioHora, fotoUrl);

            cambiarEscena(evento, raiz);
        } catch (IOException e) {
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
        } catch (IOException e) {
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