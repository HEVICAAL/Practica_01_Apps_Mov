package com.example.kotlin_listadoalumnos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperAlumno(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AlumnosDB.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ALUMNOS = "Alumnos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_ALUMNOS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NOMBRE TEXT, "
                + "$COLUMN_CUENTA TEXT, "
                + "$COLUMN_CORREO TEXT, "
                + "$COLUMN_IMAGEN TEXT)")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    fun insertarAlumno(alumno: Alumno): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val id = db.insert(TABLE_ALUMNOS, null, values)
        db.close()
        return id
    }

    fun obtenerAlumnos(): List<Alumno> {
        val listaAlumnos = ArrayList<Alumno>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_ALUMNOS ORDER BY $COLUMN_ID DESC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val nombreIndex = cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)
            val cuentaIndex = cursor.getColumnIndexOrThrow(COLUMN_CUENTA)
            val correoIndex = cursor.getColumnIndexOrThrow(COLUMN_CORREO)
            val imagenIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGEN)

            do {
                val id = cursor.getInt(idIndex)
                val nombre = cursor.getString(nombreIndex) ?: ""
                val cuenta = cursor.getString(cuentaIndex) ?: ""
                val correo = cursor.getString(correoIndex) ?: ""
                val imagen = cursor.getString(imagenIndex) ?: ""

                listaAlumnos.add(Alumno(id, nombre, cuenta, correo, imagen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaAlumnos
    }

    fun actualizarAlumno(alumno: Alumno): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val filasAfectadas = db.update(
            TABLE_ALUMNOS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(alumno.id.toString())
        )
        db.close()
        return filasAfectadas
    }

    fun eliminarAlumno(id: Int): Int {
        val db = this.writableDatabase
        val filasAfectadas = db.delete(
            TABLE_ALUMNOS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return filasAfectadas
    }
}
