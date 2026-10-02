package main.java.com.rodasdev.sport.life;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlUrl = getClass().getResource("/view/login.fxml");
            
            if (fxmlUrl == null) {
                throw new IOException("No se pudo localizar el archivo /view/login.fxml en build/classes.");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);
            primaryStage.setTitle("Sport Life - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error crítico al cargar la interfaz gráfica inicial.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}