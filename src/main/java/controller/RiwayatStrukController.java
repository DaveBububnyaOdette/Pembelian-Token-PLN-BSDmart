package main.java.controller;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.java.database.StrukDAO;
import main.java.model.Struk;

public class RiwayatStrukController {
    @FXML private TableView<Struk> strukTable;
    @FXML private TableColumn<Struk, Number> colTotal;

    private final StrukDAO strukDAO = new StrukDAO();

    @FXML
    public void initialize() {
        // Format kolom total jadi "Rp 102.500"
        colTotal.setCellFactory(col -> new TableCell<Struk, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("Rp %,.0f", item.doubleValue()));
            }
        });
        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        try {
            strukTable.setItems(FXCollections.observableArrayList(strukDAO.getAllStruk()));
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    // Memanggil method interface Cetak.struk()
    @FXML
    private void handleLihatStruk() {
        Struk selected = strukTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Pilih salah satu struk di tabel terlebih dahulu.").showAndWait();
            return;
        }
        selected.struk();
    }

    @FXML
    private void handleDelete() {
        Struk selected = strukTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Pilih salah satu struk di tabel terlebih dahulu.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus transaksi " + selected.getNoTrx() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            try {
                strukDAO.deleteStruk(selected);
                handleRefresh();
            } catch (SQLException e) {
                showError(e.getMessage());
            }
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Database Error");
        a.setContentText(msg);
        a.showAndWait();
    }
}
