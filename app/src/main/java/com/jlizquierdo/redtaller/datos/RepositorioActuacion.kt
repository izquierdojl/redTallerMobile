package com.jlizquierdo.redtaller.datos

import com.jlizquierdo.redtaller.modelo.Actuacion

interface RepositorioActuacion {
    fun elemento(id: Int): Actuacion // devuelve un tipo en base a su id
    fun tamano(): Int // devuelve el tamaño del repositorio o número de elementos
    fun getLista(): List<Actuacion> // devuelve la lista de elementos

}