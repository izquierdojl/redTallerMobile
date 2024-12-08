package com.jlizquierdo.redtaller.modelo

import java.io.Serializable
import java.util.Date

data class Actuacion (
    val id: Int = 0,
    val taller: Taller? = null,
    val cliente: Cliente? = null,
    val matricula: Matricula? = null,
    val fecha: Date? = null,
    var km: Int = 0,
    var tipo: String? = null,
) : Serializable