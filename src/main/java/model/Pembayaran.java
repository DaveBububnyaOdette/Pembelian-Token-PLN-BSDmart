package main.java.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Abstract parent class (seperti Person di contoh dosen)
public abstract class Pembayaran {
    private int idPembayaran = -1; // primary key (ditambahkan, auto-increment di DB)
    private final StringProperty jenis = new SimpleStringProperty(); // "Tunai" / "QRIS"
    private final DoubleProperty jumlah = new SimpleDoubleProperty();
    private final StringProperty tanggal = new SimpleStringProperty();
    private final StringProperty waktu = new SimpleStringProperty();

    public Pembayaran() {}

    public Pembayaran(double jumlah, String tanggal, String waktu) {
        setJumlah(jumlah);
        setTanggal(tanggal);
        setWaktu(waktu);
    }

    // Abstract method -> di-override tiap subclass (POLYMORPHISM)
    public abstract void prosesPembayaran();

    public int getIdPembayaran() { return idPembayaran; }
    public void setIdPembayaran(int id) { this.idPembayaran = id; }

    public StringProperty jenisProperty() { return jenis; }
    public String getJenis() { return jenis.get(); }
    public void setJenis(String v) { jenis.set(v); }

    public DoubleProperty jumlahProperty() { return jumlah; }
    public double getJumlah() { return jumlah.get(); }
    public void setJumlah(double v) { jumlah.set(v); }

    public StringProperty tanggalProperty() { return tanggal; }
    public String getTanggal() { return tanggal.get(); }
    public void setTanggal(String v) { tanggal.set(v); }

    public StringProperty waktuProperty() { return waktu; }
    public String getWaktu() { return waktu.get(); }
    public void setWaktu(String v) { waktu.set(v); }
}
