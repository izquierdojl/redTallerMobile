package com.jlizquierdo.redtaller.modelo

data class Actuacion_Detalle(
    val id: Int = 0,
    val id_actuacion: Int = 0,
    val linea: Int = 0,
    val descripcion: String = "",
    val existeImagen: Boolean = false,
    val imagen: ByteArray? = null
)
