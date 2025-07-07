package com.example.ezdrive.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ezdrive.model.Alamat
import com.example.ezdrive.model.User

class DBHelper(context: Context) : SQLiteOpenHelper(context, "EzDriveDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS alamat")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun registerUser(user: User): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("username", user.username)
        cv.put("email", user.email)
        cv.put("password", user.password)
        cv.put("no_hp", user.no_hp)
        cv.put("tanggal_lahir", user.tanggal_lahir)
        cv.put("role", user.role)

        val res = db.insert("users", null, cv)
        db.close()
        return res != -1L
    }

    fun login(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE email=? AND password=?",
            arrayOf(email, password)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val no_hp = cursor.getString(cursor.getColumnIndexOrThrow("no_hp"))
            val tanggal_lahir = cursor.getString(cursor.getColumnIndexOrThrow("tanggal_lahir"))
            val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))

            val alamatList = getAlamatByUser(id)

            user = User(
                id,
                username,
                email,
                password,
                alamatList,
                no_hp,
                tanggal_lahir,
                role
            )
        }
        cursor.close()
        db.close()
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
