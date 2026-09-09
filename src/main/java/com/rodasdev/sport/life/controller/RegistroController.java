package main.java.com.rodasdev.sport.life.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.java.com.rodasdev.sport.life.model.DataBaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistroController {

    @FXML private RadioButton rbUsuario;
    @FXML private RadioButton rbAdmin;
    @FXML private ToggleGroup rolToggleGroup;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private TextField txtClaveEspecial;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        if (rolToggleGroup != null && rbAdmin != null && txtClaveEspecial != null) {
            rolToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (rbAdmin.isSelected()) {
                    txtClaveEspecial.setManaged(true);
                    txtClaveEspecial.setVisible(true);
                } else {
                    txtClaveEspecial.setManaged(false);
                    txtClaveEspecial.setVisible(false);
                    txtClaveEspecial.clear();
                }
            });
        }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        String nombre = txtNombreCompleto.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmarPass = txtConfirmarPassword.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmarPass.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Por favor, completa todos los campos.");
            return;
        }

        if (!password.equals(confirmarPass)) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Las contraseñas no coinciden.");
            return;
        }

        if (rbAdmin != null && rbAdmin.isSelected()) {
            String claveEspecial = txtClaveEspecial.getText().trim();
            if (!claveEspecial.equals("ADMIN123")) { 
                lblMensaje.setStyle("-fx-text-fill: red;");
                lblMensaje.setText("Clave de administrador incorrecta.");
                return;
            }
        }

        String query = "INSERT INTO usuarios (nombre_completo, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                lblMensaje.setStyle("-fx-text-fill: green;");
                lblMensaje.setText("¡Registro exitoso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al registrar. El correo ya podría estar registrado.");
        }
    }

    @FXML
    private void handleIrALogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sport Life - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al cargar la vista de login.");
        }
    }
}