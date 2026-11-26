package main.java.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class PembayaranTunai extends Pembayaran {
    private final DoubleProperty uangDibayar = new SimpleDoubleProperty();
    private final DoubleProperty kembalian = new SimpleDoubleProperty();

    public PembayaranTunai() { setJenis("Tunai"); }

    public PembayaranTunai(double jumlah, String tanggal, String waktu, double uangDibayar) {
        super(jumlah, tanggal, waktu);
        setJenis("Tunai");
        setUangDibayar(uangDibayar);
    }

    public DoubleProperty uangDibayarProperty() { return uangDibayar; }
    public double getUangDibayar() { return uangDibayar.get(); }
    public void setUangDibayar(double v) { uangDibayar.set(v); }

    public DoubleProperty kembalianProperty() { return kembalian; }
    public double getKembalian() { return kembalian.get(); }
    public void setKembalian(double v) { kembalian.set(v); }

    public double hitungKembalian() {
        return getUangDibayar() - getJumlah();
    }

    @Override
    public void prosesPembayaran() {
        if (getUangDibayar() < getJumlah()) {
            throw new IllegalStateException("Uang yang dibayar kurang dari total tagihan!");
        }
        setKembalian(hitungKembalian());
    }
}
