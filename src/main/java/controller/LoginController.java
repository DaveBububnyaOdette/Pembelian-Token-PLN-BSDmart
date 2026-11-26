package main.java.controller;

import java.sql.SQLException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.database.KasirDAO;
import main.java.model.Kasir;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private final KasirDAO kasirDAO = new KasirDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password wajib diisi.");
            return;
        }

        try {
            Kasir kasir = kasirDAO.login(username, password);
            if (kasir == null) {
                errorLabel.setText("Username atau password salah.");
                return;
            }
            Session.kasirAktif = kasir;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/main_window.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setUser(kasir);

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("BSDmart - Token Listrik PLN");
            dashboardStage.setScene(new Scene(root, 1050, 680));
            dashboardStage.show();

            // Tutup jendela login
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loginStage.close();

            // Konfirmasi saat menutup aplikasi
            dashboardStage.setOnCloseRequest(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Keluar Aplikasi");
                alert.setHeaderText("Aplikasi akan ditutup");
                alert.setContentText("Yakin ingin keluar?");
                var result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Platform.exit();
                } else {
                    e.consume();
                }
            });
        } catch (SQLException e) {
            errorLabel.setText("Gagal terhubung ke database: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error: " + e.getMessage());
        }
    }
}
