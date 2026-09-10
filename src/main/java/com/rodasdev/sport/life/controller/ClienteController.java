package main.java.com.rodasdev.sport.life.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClienteController {

    @FXML
    private Label etiquetaBienvenida;

    @FXML
    private Button botonCerrarSesion;

    @FXML
    private Button botonReservarCancha;

    @FXML
    private Button botonMisReservas;

    private int idUsuario;
    private String nombreUsuario;

    public void inicializarUsuario(int idUsuario, String nombreUsuario) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        etiquetaBienvenida.setText("Hola, " + nombreUsuario);
    }

    @FXML
    private void alReservarCancha(ActionEvent evento) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/main/resources/view/reservar.fxml"));
            Parent raiz = cargador.load();

            ReservarController controlador = cargador.getController();
            controlador.inicializarUsuario(idUsuario, nombreUsuario);

            cambiarEscena(evento, raiz);
        } catch (Exception e) {
        }
    }

    @FXML
    private void alVerMisReservas(ActionEvent evento) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/main/resources/view/mis-reservas.fxml"));
            Parent raiz = cargador.load();

            MisReservasController controlador = cargador.getController();
            controlador.inicializarUsuario(idUsuario, nombreUsuario);

            cambiarEscena(evento, raiz);
        } catch (Exception e) {
        }
    }

    @FXML
    private void alCerrarSesion(ActionEvent evento) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/main/resources/view/login.fxml"));
            Parent raiz = cargador.load();
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