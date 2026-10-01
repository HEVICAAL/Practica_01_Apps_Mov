package com.example.kotlin_listadoalumnos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var adapter: AlumnoAdapter
    private lateinit var rvAlumnos: RecyclerView
    private lateinit var tvSinAlumnos: TextView
    private lateinit var btnAgregar: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelperAlumno(this)

        rvAlumnos = findViewById(R.id.rvAlumnos)
        tvSinAlumnos = findViewById(R.id.tvSinAlumnos)
        btnAgregar = findViewById(R.id.btnAgregar)

        // Insertar datos de prueba en la primera ejecución
        insertarDatosEjemploSiEsNecesario()

        rvAlumnos.layoutManager = LinearLayoutManager(this)
        adapter = AlumnoAdapter(
            listaAlumnos = emptyList(),
            onEditClick = { alumno -> mostrarDialogoEditar(alumno) },
            onDeleteClick = { alumno -> mostrarDialogoEliminar(alumno) }
        )
        rvAlumnos.adapter = adapter

        btnAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }

        cargarAlumnos()
    }

    private fun cargarAlumnos() {
        val lista = dbHelper.obtenerAlumnos()
        adapter.actualizarLista(lista)

        if (lista.isEmpty()) {
            tvSinAlumnos.visibility = View.VISIBLE
            rvAlumnos.visibility = View.GONE
        } else {
            tvSinAlumnos.visibility = View.GONE
            rvAlumnos.visibility = View.VISIBLE
        }
    }

    private fun insertarDatosEjemploSiEsNecesario() {
        val alumnos = dbHelper.obtenerAlumnos()
        if (alumnos.isEmpty()) {
            dbHelper.insertarAlumno(
                Alumno(
                    nombre = "Carlos González",
                    cuenta = "20231001",
                    correo = "carlos.gonzalez@unah.edu.hn",
                    imagen = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200"
                )
            )
            dbHelper.insertarAlumno(
                Alumno(
                    nombre = "María Rodríguez",
                    cuenta = "20231002",
                    correo = "maria.rodriguez@unah.edu.hn",
                    imagen = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200"
                )
            )
            dbHelper.insertarAlumno(
                Alumno(
                    nombre = "Luis Martínez",
                    cuenta = "20231003",
                    correo = "luis.martinez@unah.edu.hn",
                    imagen = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200"
                )
            )
        }
    }

    private fun mostrarDialogoAgregar() {
        val viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_alumno, null)
        val etNombre = viewDialog.findViewById<EditText>(R.id.etNombre)
        val etCuenta = viewDialog.findViewById<EditText>(R.id.etCuenta)
        val etCorreo = viewDialog.findViewById<EditText>(R.id.etCorreo)
        val etImagen = viewDialog.findViewById<EditText>(R.id.etImagen)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_agregar_titulo)
            .setView(viewDialog)
            .setPositiveButton(R.string.dialog_guardar) { dialog, _ ->
                val nombre = etNombre.text.toString().trim()
                val cuenta = etCuenta.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val imagen = etImagen.text.toString().trim()

                if (nombre.isNotEmpty() && cuenta.isNotEmpty()) {
                    val nuevoAlumno = Alumno(
                        nombre = nombre,
                        cuenta = cuenta,
                        correo = correo,
                        imagen = imagen
                    )
                    val resultado = dbHelper.insertarAlumno(nuevoAlumno)
                    if (resultado != -1L) {
                        Toast.makeText(this, "Alumno agregado correctamente", Toast.LENGTH_SHORT).show()
                        cargarAlumnos()
                    } else {
                        Toast.makeText(this, "Error al guardar el alumno", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor, ingresa Nombre y Cuenta", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancelar) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogoEditar(alumno: Alumno) {
        val viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_alumno, null)
        val etNombre = viewDialog.findViewById<EditText>(R.id.etNombre)
        val etCuenta = viewDialog.findViewById<EditText>(R.id.etCuenta)
        val etCorreo = viewDialog.findViewById<EditText>(R.id.etCorreo)
        val etImagen = viewDialog.findViewById<EditText>(R.id.etImagen)

        etNombre.setText(alumno.nombre)
        etCuenta.setText(alumno.cuenta)
        etCorreo.setText(alumno.correo)
        etImagen.setText(alumno.imagen)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_editar_titulo)
            .setView(viewDialog)
            .setPositiveButton(R.string.dialog_guardar) { dialog, _ ->
                val nombre = etNombre.text.toString().trim()
                val cuenta = etCuenta.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val imagen = etImagen.text.toString().trim()

                if (nombre.isNotEmpty() && cuenta.isNotEmpty()) {
                    val alumnoActualizado = alumno.copy(
                        nombre = nombre,
                        cuenta = cuenta,
                        correo = correo,
                        imagen = imagen
                    )
                    val filasAfectadas = dbHelper.actualizarAlumno(alumnoActualizado)
                    if (filasAfectadas > 0) {
                        Toast.makeText(this, "Alumno actualizado correctamente", Toast.LENGTH_SHORT).show()
                        cargarAlumnos()
                    } else {
                        Toast.makeText(this, "No se pudo actualizar el alumno", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor, ingresa Nombre y Cuenta", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancelar) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogoEliminar(alumno: Alumno) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_eliminar_titulo)
            .setMessage("¿Estás seguro de que deseas eliminar a ${alumno.nombre}?")
            .setPositiveButton(R.string.dialog_eliminar_confirmar) { dialog, _ ->
                val filasAfectadas = dbHelper.eliminarAlumno(alumno.id)
                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Alumno eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                } else {
                    Toast.makeText(this, "No se pudo eliminar el alumno", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancelar) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
