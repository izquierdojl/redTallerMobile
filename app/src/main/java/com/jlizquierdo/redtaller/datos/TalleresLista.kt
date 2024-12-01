package com.jlizquierdo.redtaller.datos

import com.jlizquierdo.redtaller.modelo.Taller

class TalleresLista : RepositorioTalleres {

    var listaTalleres: MutableList<Taller> = mutableListOf()

    override fun elemento(id: Int): Taller {
        return listaTalleres.get(id)
    }

    override fun tamano(): Int {
        return listaTalleres.size
    }

    override fun getLista(): List<Taller> {
        return listaTalleres;
    }
}