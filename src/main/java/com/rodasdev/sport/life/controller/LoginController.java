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
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField campoCorreo;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Button botonIniciarSesion;

    @FXML
    private Button botonIrARegistro;

    @FXML
    public void initialize() {
        etiquetaMensaje.setText("");
    }

    @FXML
    private void alIniciarSesion(ActionEvent evento) {
        String correo = campoCorreo.getText().trim();
        String contrasena = campoContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            etiquetaMensaje.setText("Completa todos los campos.");
            return;
        }

        String contrasenaHasheada = hashearContrasena(contrasena);

        try (Connection conexion = DataBaseConnection.getDataBaseConnection()) {

            if (intentarLogin(conexion, "ADMINISTRADORES", "id_admin", correo, contrasenaHasheada, evento, true)) {
                return;
            }
            if (intentarLogin(conexion, "USUARIOS", "id_usuario", correo, contrasenaHasheada, evento, false)) {
                return;
            }

            etiquetaMensaje.setStyle("-fx-text-fill: #c62828;");
            etiquetaMensaje.setText("Correo o contraseña incorrectos.");

        } catch (SQLException e) {
            etiquetaMensaje.setStyle("-fx-text-fill: #c62828;");
            etiquetaMensaje.setText("Error de conexión con la base de datos.");
        }
    }

    private boolean intentarLogin(Connection conexion, String tabla, String columnaId, String correo,
                                   String contrasenaHasheada, ActionEvent evento, boolean esAdmin) throws SQLException {

        String sql = "SELECT * FROM " + tabla + " WHERE email = ? AND password_hash = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, correo);
            sentencia.setString(2, contrasenaHasheada);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    int id = resultado.getInt(columnaId);
                    String nombre = resultado.getString("nombre_completo");

                    if (esAdmin) {
                        cambiarEscena(evento, "/main/resources/view/instalacion.fxml");
                    } else {
                        irAPanelCliente(evento, id, nombre);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @FXML
    private void alIrARegistro(ActionEvent evento) {
        cambiarEscena(evento, "/main/resources/view/registro.fxml");
    }

    private void irAPanelCliente(ActionEvent evento, int idUsuario, String nombreUsuario) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/main/resources/view/cliente.fxml"));
            Parent raiz = cargador.load();

            ClienteController controlador = cargador.getController();
            controlador.inicializarUsuario(idUsuario, nombreUsuario);

            Stage escenario = (Stage) ((Button) evento.getSource()).getScene().getWindow();
            escenario.setScene(new Scene(raiz));
            escenario.show();
        } catch (IOException e) {
            etiquetaMensaje.setText("No se pudo cargar el panel del cliente.");
        }
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