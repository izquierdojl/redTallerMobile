package com.jlizquierdo.redtaller.modelo

data class Taller(
    val nif: String,
    val nombre: String = "",
    val domicilio: String = "",
    val cp: String = "",
    val pob: String = "",
    val pro: String = "",
    val tel: String = "",
    val email: String = "",
    val movil: String = "",
    var id: Int = 0
)
