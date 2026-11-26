package main.java.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import main.java.model.Kasir;

public class DashboardController {
    @FXML private Label loggedUser;
    @FXML private TabPane mainTabPane;

    // Dipanggil dari LoginController setelah login berhasil
    public void setUser(Kasir kasir) {
        loggedUser.setText("Kasir: " + kasir.getNama() + "\n(" + kasir.getIdKasir() + ")");
        showTransaksi(); // tab pertama yang terbuka
    }

    @FXML private void showTransaksi() {
        openOrSelectTab("Transaksi Token PLN", "/main/resources/transaksi_form.fxml");
    }

    @FXML private void showPelanggan() {
        openOrSelectTab("Kelola Pelanggan", "/main/resources/pelanggan_table.fxml");
    }

    @FXML private void showKasir() {
        openOrSelectTab("Kelola Kasir", "/main/resources/kasir_table.fxml");
    }

    @FXML private void showRiwayat() {
        openOrSelectTab("Riwayat Struk", "/main/resources/riwayat_struk.fxml");
    }

    // Buka tab baru, atau pindah ke tab yang sudah terbuka
    private void openOrSelectTab(String title, String fxmlPath) {
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals(title)) {
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }
        try {
            Parent content = new FXMLLoader(getClass().getResource(fxmlPath)).load();
            Tab newTab = new Tab(title, content);
            mainTabPane.getTabs().add(newTab);
            mainTabPane.getSelectionModel().select(newTab);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka " + title + ": " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) throws Exception {
        Session.kasirAktif = null;
        Parent root = new FXMLLoader(getClass().getResource("/main/resources/login_form.fxml")).load();
        Stage loginStage = new Stage();
        loginStage.setTitle("BSDmart - Login Kasir");
        loginStage.setScene(new Scene(root, 450, 380));
        loginStage.show();

        Stage current = (Stage) ((Node) event.getSource()).getScene().getWindow();
        current.setOnCloseRequest(null); // supaya tidak muncul konfirmasi keluar
        current.close();
    }
}
