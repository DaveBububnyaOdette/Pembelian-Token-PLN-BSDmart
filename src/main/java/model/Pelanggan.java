package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pelanggan {
    private String idPelanggan; // primary key
    private final StringProperty nama = new SimpleStringProperty();

    public Pelanggan() {}

    public Pelanggan(String idPelanggan, String nama) {
        setIdPelanggan(idPelanggan);
        setNama(nama);
    }

    public String getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(String idPelanggan) { this.idPelanggan = idPelanggan; }

    public StringProperty namaProperty() { return nama; }
    public String getNama() { return nama.get(); }
    public void setNama(String value) { nama.set(value); }

    @Override
    public String toString() { return getNama() + " (" + idPelanggan + ")"; } // tampil di ComboBox
}
