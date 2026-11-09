# MAKKAE — Kitchen & Order Management

*"Made with Love, Managed with Ease."*

**MAKKAE** adalah aplikasi Android offline yang dirancang khusus untuk ibu-ibu pengusaha kuliner rumahan, katering, dan UMKM makanan (sistem PO). Aplikasi ini mempermudah pencatatan pesanan tanpa harus menuliskan di kertas, secara otomatis mengorganisir pesanan yang dicatat, serta membantu rekap jumlah porsi masakan.

---

## Preview

- **Mode Kasir**
  - Catat pesanan berdasarkan tanggal pengiriman.
  - Kontrol jumlah porsi mudah dengan *Stepper Pill*.
  - *Floating Total Bar* untuk ringkasan total harga yang siap simpan.

- **Mode Dapur**
  - Ringkasan total porsi produksi per-menu masakan untuk hari yang dipilih.
  - Visual status produksi (*Selesai*, *Sedang Dimasak*, *Belum Mulai*).
  - Filter catatan khusus pesanan (misal: "tanpa nasi", "pedas extra").

- **Export as PDF**
  - Cetak rekap produksi dan share dokumen PDF ke WhatsApp.

- **Offline & Aman**
  - Semua data tersimpan secara lokal di perangkat menggunakan Room Database.
  - Tidak memerlukan koneksi internet.

---

## Project Tree

```
app/src/main/java/com/enigma/kitchenorders/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── entity/
│   │   └── AppDatabase.kt
│   └── repository/
├── domain/
│   └── repository/
├── ui/
│   ├── components/
│   ├── screens/
│   ├── theme/
│   ├── viewmodel/
│   └── navigation/
├── util/
├── KitchenApp.kt
└── MainActivity.kt
```

---

## Prereq & build

- **Android Studio**: Android Studio Ladybug / Jellyfish (2024.1+)
- **JDK**: JDK 17
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

### Menjalankan Aplikasi:
1. Clone repo ini.
2. Buka project di Android Studio.
3. Jalankan Gradle sync.
4. Run di perangkat Android atau Emulator.
