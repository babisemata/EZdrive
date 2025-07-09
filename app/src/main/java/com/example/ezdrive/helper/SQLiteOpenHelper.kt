package com.example.ezdrive.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ezdrive.model.Alamat
import com.example.ezdrive.model.Booking
import com.example.ezdrive.model.Car
import com.example.ezdrive.model.CarCategory
import com.example.ezdrive.model.Payment
import com.example.ezdrive.model.User
import java.security.MessageDigest

class DBHelper(context: Context) : SQLiteOpenHelper(context, "EzDriveDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // TABEL USERS
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                email TEXT UNIQUE,
                password TEXT,
                no_hp TEXT,
                tanggal_lahir TEXT,
                role TEXT
            )
            """.trimIndent()
        )

        // TABEL ALAMAT
        db.execSQL(
            """
            CREATE TABLE alamat (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_user INTEGER,
                nama_penerima TEXT,
                no_hp_penerima TEXT,
                jalan TEXT,
                rt TEXT,
                rw TEXT,
                desa_kelurahan TEXT,
                kecamatan TEXT,
                kota_kabupaten TEXT,
                provinsi TEXT,
                kode_pos TEXT,
                catatan TEXT,
                FOREIGN KEY(id_user) REFERENCES users(id)
            )
            """.trimIndent()
        )

        // TABEL CAR (SUDAH DIPERBAIKI)
        db.execSQL(
            """
            CREATE TABLE Car (
                carid INTEGER PRIMARY KEY AUTOINCREMENT,
                merk TEXT,
                model TEXT,
                tahun INTEGER,
                kapasitas INTEGER,
                hargaPerHari REAL,
                foto BLOB,
                category TEXT,
                transmission TEXT,
                isAvailable INTEGER NOT NULL DEFAULT 1,
                id_user INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
        CREATE TABLE Booking (
            bookingId INTEGER PRIMARY KEY AUTOINCREMENT,
            userId INTEGER,
            carId INTEGER,
            carName TEXT,
            carImage BLOB,
            startDate TEXT,
            endDate TEXT,
            totalPrice REAL,
            status TEXT
        )
        """.trimIndent()
        )

        // TABEL CAR CATEGORY (BARU)
        db.execSQL(
            """
            CREATE TABLE CarCategory (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                iconName TEXT NOT NULL
            )
            """.trimIndent()
        )


        db.execSQL(
                """
        CREATE TABLE Payment (
            paymentId INTEGER PRIMARY KEY AUTOINCREMENT,
            bookingId INTEGER,
            amount REAL,
            paymentDate TEXT,
            status TEXT,
            FOREIGN KEY(bookingId) REFERENCES Booking(bookingId)
        )
        """.trimIndent()
            )


        // Menambahkan data kategori awal
        seedCategories(db)
    }

    private fun seedCategories(db: SQLiteDatabase) {
        val initialCategories = listOf(
            CarCategory("all", "Semua", Icons.Filled.DirectionsCar),
            CarCategory("popular", "Populer", Icons.Filled.Star),
            CarCategory("suv", "SUV", Icons.Filled.DirectionsCar),
            CarCategory("sedan", "Sedan", Icons.Filled.DriveEta),
            CarCategory("mpv", "MPV", Icons.Filled.DirectionsBus),
            CarCategory("hatchback", "Hatchback", Icons.Filled.ElectricCar)
        )
        initialCategories.forEach { category ->
            val values = ContentValues().apply {
                put("id", category.id)
                put("name", category.name)
                put("iconName", category.icon.name)
            }
            db.insert("CarCategory", null, values)
        }
    }


    private fun isTableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }


    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)

        if (!isTableExists(db, "Booking")) {
            db.execSQL("""
            CREATE TABLE Booking (
                bookingId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                carId INTEGER,
                carName TEXT,
                carImage BLOB,
                startDate TEXT,
                endDate TEXT,
                totalPrice REAL,
                status TEXT
            )
        """.trimIndent())
        }

        if (!isTableExists(db, "Payment")) {
            db.execSQL("""
            CREATE TABLE Payment (
            paymentId INTEGER PRIMARY KEY AUTOINCREMENT,
            bookingId INTEGER,
            amount REAL,
            paymentDate TEXT,
            status TEXT,
            FOREIGN KEY(bookingId) REFERENCES Booking(bookingId)
            )
        """.trimIndent())
        }
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS alamat")
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS Car")
        db.execSQL("DROP TABLE IF EXISTS CarCategory")
        db.execSQL("DROP TABLE IF EXISTS Booking")
        db.execSQL("DROP TABLE IF EXISTS Payment")

        onCreate(db)
    }

    // --- FUNGSI UNTUK MOBIL ---

    fun addBooking(booking: Booking): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("userId", booking.userId)
            put("carId", booking.carId)
            put("carName", booking.carName)
            put("carImage", booking.carImage)
            put("startDate", booking.startDate)
            put("endDate", booking.endDate)
            put("totalPrice", booking.totalPrice)
            put("status", booking.status)
        }
        val result = db.insert("Booking", null, values)
        return result != -1L
    }

    @SuppressLint("Range")
    fun getAllBookings(): List<Booking> {
        val bookingList = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Booking ORDER BY bookingId DESC", null)

        if (cursor.moveToFirst()) {
            do {
                bookingList.add(
                    Booking(
                        bookingId = cursor.getInt(cursor.getColumnIndex("bookingId")),
                        userId = cursor.getInt(cursor.getColumnIndex("userId")),
                        carId = cursor.getInt(cursor.getColumnIndex("carId")),
                        carName = cursor.getString(cursor.getColumnIndex("carName")),
                        carImage = cursor.getBlob(cursor.getColumnIndex("carImage")),
                        startDate = cursor.getString(cursor.getColumnIndex("startDate")),
                        endDate = cursor.getString(cursor.getColumnIndex("endDate")),
                        totalPrice = cursor.getDouble(cursor.getColumnIndex("totalPrice")),
                        status = cursor.getString(cursor.getColumnIndex("status"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookingList
    }


    @SuppressLint("Range")
    fun getBookingsForUser(userId: Int): List<Booking> {
        val bookingList = mutableListOf<Booking>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Booking WHERE userId = ? ORDER BY bookingId DESC", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                bookingList.add(
                    Booking(
                        bookingId = cursor.getInt(cursor.getColumnIndex("bookingId")),
                        userId = cursor.getInt(cursor.getColumnIndex("userId")),
                        carId = cursor.getInt(cursor.getColumnIndex("carId")),
                        carName = cursor.getString(cursor.getColumnIndex("carName")),
                        carImage = cursor.getBlob(cursor.getColumnIndex("carImage")),
                        startDate = cursor.getString(cursor.getColumnIndex("startDate")),
                        endDate = cursor.getString(cursor.getColumnIndex("endDate")),
                        totalPrice = cursor.getDouble(cursor.getColumnIndex("totalPrice")),
                        status = cursor.getString(cursor.getColumnIndex("status"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return bookingList
    }

    @SuppressLint("Range")
    fun getBookingById(bookingId: Int): Booking? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Booking WHERE bookingId = ?", arrayOf(bookingId.toString()))
        var booking: Booking? = null
        if (cursor.moveToFirst()) {
            booking = Booking(
                bookingId = cursor.getInt(cursor.getColumnIndex("bookingId")),
                userId = cursor.getInt(cursor.getColumnIndex("userId")),
                carId = cursor.getInt(cursor.getColumnIndex("carId")),
                carName = cursor.getString(cursor.getColumnIndex("carName")),
                carImage = cursor.getBlob(cursor.getColumnIndex("carImage")),
                startDate = cursor.getString(cursor.getColumnIndex("startDate")),
                endDate = cursor.getString(cursor.getColumnIndex("endDate")),
                totalPrice = cursor.getDouble(cursor.getColumnIndex("totalPrice")),
                status = cursor.getString(cursor.getColumnIndex("status"))
            )
        }
        cursor.close()
        return booking
    }


    fun addPayment(payment: Payment): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("bookingId", payment.bookingId)
            put("amount", payment.amount)
            put("paymentDate", payment.paymentDate)
            put("status", payment.status)
        }
        return db.insert("Payment", null, values) != -1L
    }

    fun updateBookingStatus(bookingId: Int, newStatus: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", newStatus)
        }
        val result = db.update("Booking", values, "bookingId = ?", arrayOf(bookingId.toString()))
        return result > 0
    }


    fun addCar(car: Car): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("merk", car.merk)
            put("model", car.model)
            put("tahun", car.tahun)
            put("kapasitas", car.kapasitas)
            put("hargaPerHari", car.hargaPerHari)
            put("foto", car.foto)
            put("category", car.category)
            put("transmission", car.transmission)
            put("isAvailable", if (car.isAvailable) 1 else 0)
            put("id_user", car.id_user)
        }
        val result = db.insert("Car", null, values)
        return result != -1L
    }

    // Ganti fungsi getAllCars Anda
    @SuppressLint("Range")
    fun getAllCars(): List<Car> {
        val carList = mutableListOf<Car>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Car ORDER BY carid DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val car = Car(
                    carid = cursor.getInt(cursor.getColumnIndex("carid")),
                    merk = cursor.getString(cursor.getColumnIndex("merk")),
                    model = cursor.getString(cursor.getColumnIndex("model")),
                    tahun = cursor.getInt(cursor.getColumnIndex("tahun")),
                    kapasitas = cursor.getInt(cursor.getColumnIndex("kapasitas")),
                    hargaPerHari = cursor.getDouble(cursor.getColumnIndex("hargaPerHari")),
                    foto = cursor.getBlob(cursor.getColumnIndex("foto")),
                    category = cursor.getString(cursor.getColumnIndex("category")),
                    transmission = cursor.getString(cursor.getColumnIndex("transmission")),
                    isAvailable = cursor.getInt(cursor.getColumnIndex("isAvailable")) == 1,
                    id_user = cursor.getInt(cursor.getColumnIndex("id_user"))
                )
                carList.add(car)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
    }

    @SuppressLint("Range")
    fun getCarById(carId: Int): Car? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Car WHERE carid = ?", arrayOf(carId.toString()))
        var car: Car? = null
        if (cursor.moveToFirst()) {
            car = Car(
                carid = cursor.getInt(cursor.getColumnIndex("carid")),
                merk = cursor.getString(cursor.getColumnIndex("merk")),
                model = cursor.getString(cursor.getColumnIndex("model")),
                tahun = cursor.getInt(cursor.getColumnIndex("tahun")),
                kapasitas = cursor.getInt(cursor.getColumnIndex("kapasitas")),
                hargaPerHari = cursor.getDouble(cursor.getColumnIndex("hargaPerHari")),
                foto = cursor.getBlob(cursor.getColumnIndex("foto")),
                category = cursor.getString(cursor.getColumnIndex("category")),
                transmission = cursor.getString(cursor.getColumnIndex("transmission")),
                isAvailable = cursor.getInt(cursor.getColumnIndex("isAvailable")) == 1,
                id_user = cursor.getInt(cursor.getColumnIndex("id_user"))
            )
        }
        cursor.close()
        return car
    }


    @SuppressLint("Range")
    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", arrayOf(email))
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                username = cursor.getString(cursor.getColumnIndex("username")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                password = cursor.getString(cursor.getColumnIndex("password")),
                no_hp = cursor.getString(cursor.getColumnIndex("no_hp")),
                tanggal_lahir = cursor.getString(cursor.getColumnIndex("tanggal_lahir")),
                role = cursor.getString(cursor.getColumnIndex("role"))
            )
        }
        cursor.close()
        return user
    }

    fun deleteCar(carId: Int): Boolean {
        val db = this.writableDatabase
        // Hapus mobil berdasarkan carid, dan periksa jumlah baris yang terhapus
        val result = db.delete("Car", "carid = ?", arrayOf(carId.toString()))
        return result > 0
    }

    fun updateCar(car: Car): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("merk", car.merk)
            put("model", car.model)
            put("tahun", car.tahun)
            put("kapasitas", car.kapasitas)
            put("hargaPerHari", car.hargaPerHari)
            put("foto", car.foto)
            put("category", car.category)
            put("transmission", car.transmission)
            put("isAvailable", if (car.isAvailable) 1 else 0)
        }
        // Update data berdasarkan carid
        val result = db.update("Car", values, "carid = ?", arrayOf(car.carid.toString()))
        return result > 0
    }

    @SuppressLint("Range")
    fun searchCars(query: String): List<Car> {
        val carList = mutableListOf<Car>()
        val db = this.readableDatabase
        // Gunakan LIKE untuk mencari mobil yang merk atau modelnya mengandung query
        val cursor = db.rawQuery(
            "SELECT * FROM Car WHERE merk LIKE ? OR model LIKE ?",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val car = Car(
                    carid = cursor.getInt(cursor.getColumnIndex("carid")),
                    merk = cursor.getString(cursor.getColumnIndex("merk")),
                    model = cursor.getString(cursor.getColumnIndex("model")),
                    tahun = cursor.getInt(cursor.getColumnIndex("tahun")),
                    kapasitas = cursor.getInt(cursor.getColumnIndex("kapasitas")),
                    hargaPerHari = cursor.getDouble(cursor.getColumnIndex("hargaPerHari")),
                    foto = cursor.getBlob(cursor.getColumnIndex("foto")),
                    category = cursor.getString(cursor.getColumnIndex("category")),
                    transmission = cursor.getString(cursor.getColumnIndex("transmission")),
                    isAvailable = cursor.getInt(cursor.getColumnIndex("isAvailable")) == 1,
                    id_user = cursor.getInt(cursor.getColumnIndex("id_user"))
                )
                carList.add(car)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return carList
    }

    // --- FUNGSI UNTUK KATEGORI ---

    @SuppressLint("Range")
    fun getAllCategories(): List<CarCategory> {
        val categoryList = mutableListOf<CarCategory>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM CarCategory", null)

        if (cursor.moveToFirst()) {
            do {
                val category = CarCategory(
                    id = cursor.getString(cursor.getColumnIndex("id")),
                    name = cursor.getString(cursor.getColumnIndex("name")),
                    icon = mapIconNameToVector(cursor.getString(cursor.getColumnIndex("iconName")))
                )
                categoryList.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return categoryList
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun mapIconNameToVector(iconName: String): ImageVector {
        return when (iconName) {
            "Filled.DirectionsCar" -> Icons.Filled.DirectionsCar
            "Filled.Star" -> Icons.Filled.Star
            "Filled.DriveEta" -> Icons.Filled.DriveEta
            "Filled.DirectionsBus" -> Icons.Filled.DirectionsBus
            "Filled.ElectricCar" -> Icons.Filled.ElectricCar
            else -> Icons.Filled.HelpOutline
        }
    }

    fun registerUser(user: User): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("username", user.username)
        cv.put("email", user.email)
        // Hash password sebelum disimpan
        cv.put("password", hashPassword(user.password))
        cv.put("no_hp", user.no_hp)
        cv.put("tanggal_lahir", user.tanggal_lahir)
        cv.put("role", user.role)

        val res = db.insert("users", null, cv)
        return res != -1L
    }

    fun login(email: String, password: String): User? {
        val db = readableDatabase
        // 1. Cari user berdasarkan email saja
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE email=?",
            arrayOf(email)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val storedHashedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            // 2. Hash password yang diinput pengguna saat login
            val inputHashedPassword = hashPassword(password)

            // 3. Bandingkan hash yang tersimpan dengan hash dari input
            if (storedHashedPassword == inputHashedPassword) {
                // Jika cocok, baru ambil data user
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val no_hp = cursor.getString(cursor.getColumnIndexOrThrow("no_hp"))
                val tanggal_lahir = cursor.getString(cursor.getColumnIndexOrThrow("tanggal_lahir"))
                val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                val alamatList = getAlamatByUser(id)

                user = User(id, username, email, "", alamatList, no_hp, tanggal_lahir, role)
            }
        }
        cursor.close()
        return user
    }

    fun insertAlamat(alamat: Alamat): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("id_user", alamat.id_user)
        cv.put("nama_penerima", alamat.nama_penerima)
        cv.put("no_hp_penerima", alamat.no_hp_penerima)
        cv.put("jalan", alamat.jalan)
        cv.put("rt", alamat.rt)
        cv.put("rw", alamat.rw)
        cv.put("desa_kelurahan", alamat.desa_kelurahan)
        cv.put("kecamatan", alamat.kecamatan)
        cv.put("kota_kabupaten", alamat.kota_kabupaten)
        cv.put("provinsi", alamat.provinsi)
        cv.put("kode_pos", alamat.kode_pos)
        cv.put("catatan", alamat.catatan)
        val res = db.insert("alamat", null, cv)
        db.close()
        return res != -1L
    }

    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM users WHERE email=?", arrayOf(email))
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun getUserIdByEmail(email: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE email=?",
            arrayOf(email)
        )
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return id
    }

    private fun getAlamatByUser(userId: Int): List<Alamat> {
        val list = mutableListOf<Alamat>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM alamat WHERE id_user=?",
            arrayOf(userId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Alamat(
                        id_user = userId,
                        nama_penerima = cursor.getString(cursor.getColumnIndexOrThrow("nama_penerima")),
                        no_hp_penerima = cursor.getString(cursor.getColumnIndexOrThrow("no_hp_penerima")),
                        jalan = cursor.getString(cursor.getColumnIndexOrThrow("jalan")),
                        rt = cursor.getString(cursor.getColumnIndexOrThrow("rt")),
                        rw = cursor.getString(cursor.getColumnIndexOrThrow("rw")),
                        desa_kelurahan = cursor.getString(cursor.getColumnIndexOrThrow("desa_kelurahan")),
                        kecamatan = cursor.getString(cursor.getColumnIndexOrThrow("kecamatan")),
                        kota_kabupaten = cursor.getString(cursor.getColumnIndexOrThrow("kota_kabupaten")),
                        provinsi = cursor.getString(cursor.getColumnIndexOrThrow("provinsi")),
                        kode_pos = cursor.getString(cursor.getColumnIndexOrThrow("kode_pos")),
                        catatan = cursor.getString(cursor.getColumnIndexOrThrow("catatan")),
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}
