package main.java.com.rodasdev.sport.life.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.com.rodasdev.sport.life.model.DataBaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;
    @FXML private Button btnLogin;
    @FXML private Button btnIrARegistro;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Por favor, completa todos los campos.");
            return;
        }
        
        String query = "SELECT nombre_completo FROM usuarios WHERE email = ? AND password_hash = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre_completo");
                    lblMensaje.setStyle("-fx-text-fill: green;");
                    lblMensaje.setText("¡Bienvenido, " + nombre + "!");
                } else {
                    lblMensaje.setStyle("-fx-text-fill: red;");
                    lblMensaje.setText("Correo o contraseña incorrectos.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error de conexión con la base de datos.");
        }
    }

    @FXML
    private void handleIrARegistro(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/registro.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sport Life - Registro");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al cargar la vista de registro.");
        }
    }
}