package com.jlizquierdo.redtaller.datos

import com.jlizquierdo.redtaller.modelo.Taller

interface RepositorioTalleres {
    fun elemento(id: Int): Taller // devuelve un tipo en base a su id
    fun tamano(): Int // devuelve el tamaño del repositorio o número de elementos
    fun getLista(): List<Taller> // devuelve la lista de elementos
}
