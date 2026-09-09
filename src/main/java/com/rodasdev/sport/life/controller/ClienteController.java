package main.java.com.rodasdev.sport.life.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

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
    }

    @FXML
    private void alVerMisReservas(ActionEvent evento) {
    }

    @FXML
    private void alCerrarSesion(ActionEvent evento) {
        cambiarEscena(evento, "/main/resources/view/login.fxml");
    }

    private void cambiarEscena(ActionEvent evento, String rutaFxml) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource(rutaFxml));
            Stage escenario = (Stage) ((Button) evento.getSource()).getScene().getWindow();
            escenario.setScene(new Scene(raiz));
            escenario.show();
        } catch (IOException e) {
        }
    }
}