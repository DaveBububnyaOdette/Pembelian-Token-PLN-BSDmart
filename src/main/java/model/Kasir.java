package main.java.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Kasir {
    private String idKasir; // primary key
    private final StringProperty nama = new SimpleStringProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    public Kasir() {}

    public Kasir(String idKasir, String nama, String username, String password) {
        setIdKasir(idKasir);
        setNama(nama);
        setUsername(username);
        setPassword(password);
    }

    public String getIdKasir() { return idKasir; }
    public void setIdKasir(String idKasir) { this.idKasir = idKasir; }

    public StringProperty namaProperty() { return nama; }
    public String getNama() { return nama.get(); }
    public void setNama(String value) { nama.set(value); }

    public StringProperty usernameProperty() { return username; }
    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }

    public StringProperty passwordProperty() { return password; }
    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }

    @Override
    public String toString() { return getNama() + " (" + idKasir + ")"; }
}
