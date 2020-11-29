package com.pet001kambala.namopsfleetmanager.utils

enum class Docs(private val value: String) {
    ACCOUNTS("Accounts"),
    VEHICLES("Vehicles"),
    TYRES("Tyres"),
    TYRE_SURVEY("Tyre Survey"),
    TYRE_REPAIR("Tyre Repair"),
    TYRE_MOUNT("Tyre Mount"),
    TRAILER("Trailer"),
    TYRE_VENDORS("Tyre Vendors");


    override fun toString(): String {
        return value
    }
}