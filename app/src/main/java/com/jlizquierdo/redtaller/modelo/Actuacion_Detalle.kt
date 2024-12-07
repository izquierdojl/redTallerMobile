package com.jlizquierdo.redtaller.modelo

data class Actuacion_Detalle(
    val id_actuacion: Int = 0,
    val linea: Int = 0,
    val descripcion: String = "",
    val imagen: ByteArray
)
