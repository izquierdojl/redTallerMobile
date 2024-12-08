package com.jlizquierdo.redtaller.modelo

import java.io.Serializable

data class Matricula(
    val matricula: String,
    val marca: String = "",
    val modelo: String = "",
) : Serializable