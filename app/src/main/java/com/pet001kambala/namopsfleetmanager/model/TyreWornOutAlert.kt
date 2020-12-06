package com.pet001kambala.namopsfleetmanager.model

class TyreWornOutAlert: AbstractModel() {
    lateinit var tyre: Tyre

    override fun toString(): String {
        return "${tyre.serialNumber} | ${tyre.brand} | ${tyre.size}"
    }
}