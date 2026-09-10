package main.java.com.rodasdev.sport.life.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import main.java.com.rodasdev.sport.life.model.DataBaseConnection;
import main.java.com.rodasdev.sport.life.model.InstalacionDeportiva;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class InstalacionController {

    @FXML private TableView<InstalacionDeportiva> tablaInstalaciones;
    @FXML private TableColumn<InstalacionDeportiva, String> colCodigo;
    @FXML private TableColumn<InstalacionDeportiva, String> colDescripcion;
    @FXML private TableColumn<InstalacionDeportiva, Number> colTipoDeporte;
    @FXML private TableColumn<InstalacionDeportiva, Number> colAdmin;
    @FXML private TableColumn<InstalacionDeportiva, Boolean> colTechada;
    @FXML private TableColumn<InstalacionDeportiva, Number> colPrecio;
    @FXML private TableColumn<InstalacionDeportiva, Boolean> colHabilitada;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtTipoDeporte;
    @FXML private TextField txtAdmin;
    @FXML private TextField txtDescripcion;
    @FXML private CheckBox chkTechada;
    @FXML private TextField txtPrecioHora;
    @FXML private TextField txtFotoUrl;
    @FXML private CheckBox chkHabilitada;
    @FXML private Label lblMensaje;
    @FXML private ImageView imgVistaPrevia;
    @FXML private Button btnSeleccionarFoto;

    private ObservableList<InstalacionDeportiva> listaInstalaciones = FXCollections.observableArrayList();
    private InstalacionDeportiva instalacionSeleccionada = null;
    private File archivoFotoSeleccionado = null;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdCodigoInstalacion()));
        colDescripcion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));
        colTipoDeporte.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdTipoDeporte()));
        colAdmin.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdAdmin()));
        colTechada.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isEsTechada()));
        colPrecio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrecioHora()));
        colHabilitada.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isHabilitada()));

        cargarInstalaciones();

        tablaInstalaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                instalacionSeleccionada = newVal;
                txtCodigo.setText(newVal.getIdCodigoInstalacion());
                txtCodigo.setDisable(true);
                txtTipoDeporte.setText(String.valueOf(newVal.getIdTipoDeporte()));
                txtAdmin.setText(String.valueOf(newVal.getIdAdmin()));
                txtDescripcion.setText(newVal.getDescripcion());
                chkTechada.setSelected(newVal.isEsTechada());
                txtPrecioHora.setText(String.valueOf(newVal.getPrecioHora()));
                txtFotoUrl.setText(newVal.getFotoUrl());
                chkHabilitada.setSelected(newVal.isHabilitada());
                archivoFotoSeleccionado = null;
                mostrarVistaPreviaExistente(newVal.getFotoUrl());
            }
        });

        limpiarCampos();
    }

    @FXML
    private void handleSeleccionarFoto() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Selecciona la foto de la cancha");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File archivo = selector.showOpenDialog(btnSeleccionarFoto.getScene().getWindow());
        if (archivo != null) {
            archivoFotoSeleccionado = archivo;
            imgVistaPrevia.setImage(new Image(archivo.toURI().toString()));
        }
    }

    private void mostrarVistaPreviaExistente(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            imgVistaPrevia.setImage(null);
            return;
        }
        InputStream recurso = getClass().getResourceAsStream("/main/resources/imagenes/" + nombreArchivo);
        if (recurso != null) {
            imgVistaPrevia.setImage(new Image(recurso));
        } else {
            imgVistaPrevia.setImage(null);
        }
    }

    private String copiarFotoAResources(String codigoInstalacion) {
        if (archivoFotoSeleccionado == null) {
            return txtFotoUrl.getText().trim();
        }
        try {
            String nombreOriginal = archivoFotoSeleccionado.getName();
            String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
            String nombreArchivo = codigoInstalacion + extension;

            Path carpetaFuente = Paths.get("src/main/resources/imagenes");
            Files.createDirectories(carpetaFuente);
            Files.copy(archivoFotoSeleccionado.toPath(), carpetaFuente.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);

            URL recursoClasses = getClass().getResource("/main/resources/imagenes/");
            if (recursoClasses != null) {
                Path carpetaClasses = Paths.get(recursoClasses.toURI());
                Files.copy(archivoFotoSeleccionado.toPath(), carpetaClasses.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
            }

            return nombreArchivo;
        } catch (Exception e) {
            e.printStackTrace();
            return txtFotoUrl.getText().trim();
        }
    }

    @FXML
    private void cargarInstalaciones() {
        listaInstalaciones.clear();
        String query = "SELECT * FROM instalaciones_deportivas";

        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                listaInstalaciones.add(new InstalacionDeportiva(
                        rs.getString("id_codigo_instalacion"),
                        rs.getInt("id_tipo_deporte"),
                        rs.getInt("id_admin"),
                        rs.getString("descripcion"),
                        rs.getBoolean("es_techada"),
                        rs.getDouble("precio_hora"),
                        rs.getString("foto_url"),
                        rs.getBoolean("habilitada")
                ));
            }
            tablaInstalaciones.setItems(listaInstalaciones);
        } catch (SQLException e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al cargar las instalaciones de la base de datos.");
        }
    }

    @FXML
    private void handleCrear() {
        String nombreFoto = copiarFotoAResources(txtCodigo.getText().trim());
        String query = "INSERT INTO instalaciones_deportivas (id_codigo_instalacion, id_tipo_deporte, id_admin, descripcion, es_techada, precio_hora, foto_url, habilitada) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, txtCodigo.getText().trim());
            pstmt.setInt(2, Integer.parseInt(txtTipoDeporte.getText().trim()));
            pstmt.setInt(3, Integer.parseInt(txtAdmin.getText().trim()));
            pstmt.setString(4, txtDescripcion.getText().trim());
            pstmt.setBoolean(5, chkTechada.isSelected());
            pstmt.setDouble(6, Double.parseDouble(txtPrecioHora.getText().trim()));
            pstmt.setString(7, nombreFoto);
            pstmt.setBoolean(8, chkHabilitada.isSelected());

            pstmt.executeUpdate();
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("¡Instalación creada exitosamente!");
            cargarInstalaciones();
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al crear. Verifica que el código no esté repetido.");
        }
    }

    @FXML
    private void handleActualizar() {
        if (instalacionSeleccionada == null) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Selecciona una instalación de la tabla para actualizar.");
            return;
        }

        String nombreFoto = copiarFotoAResources(instalacionSeleccionada.getIdCodigoInstalacion());
        String query = "UPDATE instalaciones_deportivas SET id_tipo_deporte = ?, id_admin = ?, descripcion = ?, es_techada = ?, precio_hora = ?, foto_url = ?, habilitada = ? WHERE id_codigo_instalacion = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(txtTipoDeporte.getText().trim()));
            pstmt.setInt(2, Integer.parseInt(txtAdmin.getText().trim()));
            pstmt.setString(3, txtDescripcion.getText().trim());
            pstmt.setBoolean(4, chkTechada.isSelected());
            pstmt.setDouble(5, Double.parseDouble(txtPrecioHora.getText().trim()));
            pstmt.setString(6, nombreFoto);
            pstmt.setBoolean(7, chkHabilitada.isSelected());
            pstmt.setString(8, instalacionSeleccionada.getIdCodigoInstalacion());

            pstmt.executeUpdate();
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("¡Instalación actualizada exitosamente!");
            cargarInstalaciones();
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al actualizar la instalación.");
        }
    }

    @FXML
    private void handleDeshabilitar() {
        if (instalacionSeleccionada == null) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Selecciona una instalación para deshabilitar.");
            return;
        }

        String query = "UPDATE instalaciones_deportivas SET habilitada = 0 WHERE id_codigo_instalacion = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, instalacionSeleccionada.getIdCodigoInstalacion());
            pstmt.executeUpdate();
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("Instalación deshabilitada correctamente.");
            cargarInstalaciones();
            limpiarCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al deshabilitar la instalación.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtCodigo.clear();
        txtCodigo.setDisable(false);
        txtTipoDeporte.clear();
        txtAdmin.clear();
        txtDescripcion.clear();
        chkTechada.setSelected(false);
        txtPrecioHora.clear();
        txtFotoUrl.clear();
        chkHabilitada.setSelected(true);
        instalacionSeleccionada = null;
        archivoFotoSeleccionado = null;
        imgVistaPrevia.setImage(null);
        if (tablaInstalaciones != null) {
            tablaInstalaciones.getSelectionModel().clearSelection();
        }
    }
}