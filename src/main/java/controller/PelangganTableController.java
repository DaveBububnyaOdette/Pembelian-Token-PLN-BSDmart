package main.java.controller;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import main.java.database.PelangganDAO;
import main.java.model.Pelanggan;

public class PelangganTableController {
    @FXML private TextField idField;
    @FXML private TextField namaField;
    @FXML private TableView<Pelanggan> pelangganTable;

    private final PelangganDAO pelangganDAO = new PelangganDAO();

    @FXML
    public void initialize() {
        refreshTable();
        // Klik baris tabel -> isi form (ID dikunci karena primary key)
        pelangganTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                idField.setText(newV.getIdPelanggan());
                idField.setDisable(true);
                namaField.setText(newV.getNama());
            }
        });
    }

    private void refreshTable() {
        try {
            pelangganTable.setItems(FXCollections.observableArrayList(pelangganDAO.getAllPelanggan()));
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        if (id.isEmpty() || nama.isEmpty()) {
            showWarning("ID dan Nama pelanggan wajib diisi!");
            return;
        }
        try {
            pelangganDAO.savePelanggan(new Pelanggan(id, nama));
            refreshTable();
            handleClear();
        } catch (SQLException e) {
            showError("Gagal menyimpan (ID mungkin sudah dipakai)", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Pelanggan selected = pelangganTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih baris pelanggan di tabel terlebih dahulu.");
            return;
        }
        if (namaField.getText().trim().isEmpty()) {
            showWarning("Nama pelanggan wajib diisi!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin mengubah data ini?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try {
                pelangganDAO.updatePelanggan(new Pelanggan(selected.getIdPelanggan(), namaField.getText().trim()));
                refreshTable();
                handleClear();
            } catch (SQLException e) {
                showError("Gagal mengubah data", e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete() {
        Pelanggan selected = pelangganTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Pilih baris pelanggan di tabel terlebih dahulu.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus " + selected.getNama() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try {
                pelangganDAO.deletePelanggan(selected.getIdPelanggan());
                refreshTable();
                handleClear();
            } catch (SQLException e) {
                showError("Tidak bisa dihapus", "Pelanggan ini sudah memiliki transaksi (struk).\n\n" + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        idField.clear();
        idField.setDisable(false);
        namaField.clear();
        pelangganTable.getSelectionModel().clearSelection();
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
