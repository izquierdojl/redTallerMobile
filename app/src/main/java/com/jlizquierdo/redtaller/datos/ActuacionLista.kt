package com.jlizquierdo.redtaller.datos

import com.jlizquierdo.redtaller.modelo.Actuacion

class ActuacionLista : RepositorioActuacion {

    var listaActuacion: MutableList<Actuacion> = mutableListOf()

    override fun elemento(id: Int): Actuacion {
        return listaActuacion.get((id))    }

    override fun tamano(): Int {
        return listaActuacion.size
    }

    override fun getLista(): List<Actuacion> {
        return listaActuacion
    }

}