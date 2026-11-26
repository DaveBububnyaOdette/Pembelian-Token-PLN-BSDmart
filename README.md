# BSDmart - Pembelian Token Listrik PLN Prabayar

Aplikasi kasir berbasis JavaFX (MVC) dengan database PostgreSQL untuk transaksi pembelian token listrik PLN prabayar.
Proyek GUI mata kuliah Pemrograman Berorientasi Objek, Universitas Tarumanagara.

**Kelompok:**
- **B**enz Christian Immanuel (825250104)
- **S**amuel Lie (825250102)
- **D**ave Joseph Immanuel Tanuwidjaja (825250150)

## Fitur
- Login kasir (password disimpan dengan hash BCrypt)
- Transaksi token PLN, pembayaran Tunai atau QRIS, cetak struk
- Kelola Pelanggan dan Kelola Kasir (tambah, ubah, hapus)
- Riwayat struk

## Cara menjalankan
1. **Database:** buat database `db_bsdmart_pln` di pgAdmin, lalu jalankan `bsdmart_pln.sql` lewat Query Tool.
2. **Koneksi:** salin `db.properties.example` menjadi `db.properties`, lalu isi user dan password PostgreSQL.
3. **JavaFX SDK:** sesuaikan path `--module-path` di `.vscode/launch.json` dan path `javafx-sdk` di `.vscode/settings.json` dengan lokasi JavaFX SDK di komputermu.
4. **Library:** `postgresql-*.jar` dan `jbcrypt-0.4.jar` sudah ada di folder `lib/`.
5. Buka Run and Debug (Ctrl+Shift+D), pilih **Main (GUI)**, lalu tekan F5.

Akun contoh: `budi` / `budi123` atau `siti` / `siti123`.

## Struktur
- `src/main/java/model` : Kasir, Pelanggan, PLNPraBayar, Pembayaran (abstract), PembayaranTunai, PembayaranQris, Struk, Cetak (interface)
- `src/main/java/database` : koneksi dan DAO
- `src/main/java/controller` : controller JavaFX
- `src/main/resources` : layout `.fxml` dan `css/style.css`
