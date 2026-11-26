package main.java.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PLNPraBayar {
    private int idPln = -1; // primary key (ditambahkan, auto-increment di DB)
    private final StringProperty noMeter = new SimpleStringProperty();
    private final StringProperty idPel = new SimpleStringProperty();
    private final StringProperty nama = new SimpleStringProperty();
    private final StringProperty tarifDaya = new SimpleStringProperty();
    private final StringProperty noRef = new SimpleStringProperty();
    private final StringProperty token = new SimpleStringProperty();
    private final DoubleProperty jumlahKwh = new SimpleDoubleProperty();
    private final DoubleProperty nominalToken = new SimpleDoubleProperty();
    private final DoubleProperty biayaAdmin = new SimpleDoubleProperty();

    public PLNPraBayar() {}

    public PLNPraBayar(String noMeter, String idPel, String nama, String tarifDaya,
                       String noRef, String token, double jumlahKwh,
                       double nominalToken, double biayaAdmin) {
        setNoMeter(noMeter);
        setIdPel(idPel);
        setNama(nama);
        setTarifDaya(tarifDaya);
        setNoRef(noRef);
        setToken(token);
        setJumlahKwh(jumlahKwh);
        setNominalToken(nominalToken);
        setBiayaAdmin(biayaAdmin);
    }

    public int getIdPln() { return idPln; }
    public void setIdPln(int idPln) { this.idPln = idPln; }

    public StringProperty noMeterProperty() { return noMeter; }
    public String getNoMeter() { return noMeter.get(); }
    public void setNoMeter(String v) { noMeter.set(v); }

    public StringProperty idPelProperty() { return idPel; }
    public String getIdPel() { return idPel.get(); }
    public void setIdPel(String v) { idPel.set(v); }

    public StringProperty namaProperty() { return nama; }
    public String getNama() { return nama.get(); }
    public void setNama(String v) { nama.set(v); }

    public StringProperty tarifDayaProperty() { return tarifDaya; }
    public String getTarifDaya() { return tarifDaya.get(); }
    public void setTarifDaya(String v) { tarifDaya.set(v); }

    public StringProperty noRefProperty() { return noRef; }
    public String getNoRef() { return noRef.get(); }
    public void setNoRef(String v) { noRef.set(v); }

    public StringProperty tokenProperty() { return token; }
    public String getToken() { return token.get(); }
    public void setToken(String v) { token.set(v); }

    public DoubleProperty jumlahKwhProperty() { return jumlahKwh; }
    public double getJumlahKwh() { return jumlahKwh.get(); }
    public void setJumlahKwh(double v) { jumlahKwh.set(v); }

    public DoubleProperty nominalTokenProperty() { return nominalToken; }
    public double getNominalToken() { return nominalToken.get(); }
    public void setNominalToken(double v) { nominalToken.set(v); }

    public DoubleProperty biayaAdminProperty() { return biayaAdmin; }
    public double getBiayaAdmin() { return biayaAdmin.get(); }
    public void setBiayaAdmin(double v) { biayaAdmin.set(v); }
}
