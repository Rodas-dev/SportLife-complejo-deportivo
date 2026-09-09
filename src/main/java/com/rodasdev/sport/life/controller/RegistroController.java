package main.java.com.rodasdev.sport.life.controller;

import main.java.com.rodasdev.sport.life.model.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistroController {

    private static final String CLAVE_ADMIN = "2026330";

    @FXML
    private TextField campoNombreCompleto;

    @FXML
    private TextField campoCorreo;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private PasswordField campoConfirmarContrasena;

    @FXML
    private TextField campoClaveEspecial;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Button botonRegistrar;

    @FXML
    private Button botonIrALogin;

    @FXML
    public void initialize() {
        etiquetaMensaje.setText("");
    }

    @FXML
    private void alRegistrar(ActionEvent evento) {
        String nombre = campoNombreCompleto.getText().trim();
        String correo = campoCorreo.getText().trim();
        String contrasena = campoContrasena.getText();
        String confirmacion = campoConfirmarContrasena.getText();
        String claveIngresada = campoClaveEspecial.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmacion.isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }

        if (!contrasena.equals(confirmacion)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        boolean quiereSerAdmin = !claveIngresada.isEmpty();

        if (quiereSerAdmin && !CLAVE_ADMIN.equals(claveIngresada)) {
            mostrarError("Clave de administrador incorrecta.");
            return;
        }

        String tabla = quiereSerAdmin ? "ADMINISTRADORES" : "USUARIOS";
        String sql = "INSERT INTO " + tabla + " (nombre_completo, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conexion = DataBaseConnection.getDataBaseConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, correo);
            sentencia.setString(3, hashearContrasena(contrasena));

            sentencia.executeUpdate();

            etiquetaMensaje.setStyle("-fx-text-fill: #2e7d32;");
            etiquetaMensaje.setText("Cuenta creada con éxito. Ya puedes iniciar sesión.");

        } catch (SQLException e) {
            mostrarError("No se pudo registrar. Verifica si el correo ya existe.");
        }
    }

    @FXML
    private void alIrALogin(ActionEvent evento) {
        cambiarEscena(evento, "/main/resources/view/login.fxml");
    }

    private void mostrarError(String mensaje) {
        etiquetaMensaje.setStyle("-fx-text-fill: #c62828;");
        etiquetaMensaje.setText(mensaje);
    }

    private void cambiarEscena(ActionEvent evento, String rutaFxml) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource(rutaFxml));
            Stage escenario = (Stage) ((Button) evento.getSource()).getScene().getWindow();
            escenario.setScene(new Scene(raiz));
            escenario.show();
        } catch (IOException e) {
            etiquetaMensaje.setText("No se pudo cargar la vista.");
        }
    }

    private String hashearContrasena(String contrasena) {
        try {
            MessageDigest digestor = MessageDigest.getInstance("SHA-256");
            byte[] bytesHash = digestor.digest(contrasena.getBytes());
            StringBuilder constructor = new StringBuilder();
            for (byte b : bytesHash) {
                constructor.append(String.format("%02x", b));
            }
            return constructor.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}