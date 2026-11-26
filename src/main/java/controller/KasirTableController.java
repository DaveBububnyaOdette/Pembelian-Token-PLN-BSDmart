package main.java.controller;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import main.java.database.KasirDAO;
import main.java.model.Kasir;

public class KasirTableController {
    private static final int MIN_PASSWORD = 6;

    @FXML private TextField idField;
    @FXML private TextField namaField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TableView<Kasir> kasirTable;

    private final KasirDAO kasirDAO = new KasirDAO();

    @FXML
    public void initialize() {
        refreshTable();
        // Klik baris tabel -> isi form (ID dikunci karena primary key, password dikosongkan)
        kasirTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                idField.setText(newV.getIdKasir());
                idField.setDisable(true);
                namaField.setText(newV.getNama());
                usernameField.setText(newV.getUsername());
                passwordField.clear();
                passwordField.setPromptText("Kosongkan jika tidak diubah");
            }
        });
    }

    private void refreshTable() {
        try {
            kasirTable.setItems(FXCollections.observableArrayList(kasirDAO.getAllKasir()));
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (id.isEmpty() || nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showWarning("ID, Nama, Username, dan Password wajib diisi!");
            return;
        }
        if (password.length() < MIN_PASSWORD) {
            showWarning("Password minimal " + MIN_PASSWORD + " karakter.");
            return;
        }
        try {
            kasirDAO.saveKasir(new Kasir(id, nama, username, password));
            refreshTable();
            handleClear();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // unique_violation
                showError("Gagal menyimpan", "ID Kasir atau Username sudah dipakai kasir lain.");
            } else {
                showError("Gagal menyimpan", e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdate() {
        Kasir selected = kasirTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih baris kasir di tabel terlebih dahulu.");
            return;
        }
        String nama = namaField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        boolean ubahPassword = !password.isEmpty();

        if (nama.isEmpty() || username.isEmpty()) {
            showWarning("Nama dan Username wajib diisi!");
            return;
        }
        if (ubahPassword && password.length() < MIN_PASSWORD) {
            showWarning("Password baru minimal " + MIN_PASSWORD + " karakter.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin mengubah data kasir ini?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try {
                kasirDAO.updateKasir(new Kasir(selected.getIdKasir(), nama, username, password), ubahPassword);
                // Jika yang diubah adalah kasir yang sedang login, samakan datanya di Session
                if (Session.kasirAktif != null && Session.kasirAktif.getIdKasir().equals(selected.getIdKasir())) {
                    Session.kasirAktif.setNama(nama);
                    Session.kasirAktif.setUsername(username);
                }
                refreshTable();
                handleClear();
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    showError("Gagal mengubah", "Username sudah dipakai kasir lain.");
                } else {
                    showError("Gagal mengubah data", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleDelete() {
        Kasir selected = kasirTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih baris kasir di tabel terlebih dahulu.");
            return;
        }
        if (Session.kasirAktif != null && Session.kasirAktif.getIdKasir().equals(selected.getIdKasir())) {
            showWarning("Kasir yang sedang login tidak bisa dihapus.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus kasir " + selected.getNama() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try {
                kasirDAO.deleteKasir(selected.getIdKasir());
                refreshTable();
                handleClear();
            } catch (SQLException e) {
                if ("23503".equals(e.getSQLState())) { // foreign_key_violation
                    showError("Tidak bisa dihapus", "Kasir ini sudah pernah membuat transaksi (struk).");
                } else {
                    showError("Gagal menghapus", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        idField.clear();
        idField.setDisable(false);
        namaField.clear();
        usernameField.clear();
        passwordField.clear();
        passwordField.setPromptText("Minimal " + MIN_PASSWORD + " karakter");
        kasirTable.getSelectionModel().clearSelection();
    }

    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}
