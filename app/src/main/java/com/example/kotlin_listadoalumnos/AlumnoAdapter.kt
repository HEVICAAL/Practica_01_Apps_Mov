package com.example.kotlin_listadoalumnos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AlumnoAdapter(
    private var listaAlumnos: List<Alumno>,
    private val onEditClick: (Alumno) -> Unit,
    private val onDeleteClick: (Alumno) -> Unit,
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPersona: ImageView = itemView.findViewById(R.id.imgPersona)
        val tvNombrePersona: TextView = itemView.findViewById(R.id.tvNombrePersona)
        val tvCuentaPersona: TextView = itemView.findViewById(R.id.tvCuentaPersona)
        val tvCorreoPersona: TextView = itemView.findViewById(R.id.tvCorreoPersona)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_persona, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = listaAlumnos[position]
        val context = holder.itemView.context

        holder.tvNombrePersona.text = alumno.nombre
        holder.tvCuentaPersona.text = context.getString(R.string.formato_cuenta, alumno.cuenta)
        holder.tvCorreoPersona.text = alumno.correo

        // Cargar la imagen utilizando la librería Glide
        if (alumno.imagen.isNotBlank()) {
            Glide.with(context)
                .load(alumno.imagen)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .into(holder.imgPersona)
        } else {
            holder.imgPersona.setImageResource(R.drawable.ic_person_placeholder)
        }

        holder.btnEditar.setOnClickListener {
            onEditClick(alumno)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(alumno)
        }
    }

    override fun getItemCount(): Int = listaAlumnos.size

    fun actualizarLista(nuevaLista: List<Alumno>) {
        this.listaAlumnos = nuevaLista
        notifyDataSetChanged()
    }
}
