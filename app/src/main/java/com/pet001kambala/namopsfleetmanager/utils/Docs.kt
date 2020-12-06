package com.pet001kambala.namopsfleetmanager.utils

enum class Docs(private val value: String) {
    ACCOUNTS("Accounts"),
    VEHICLES("Vehicles"),
    TYRES("Tyres"),
    TYRE_INSPECTION("Tyre Inspection"),
    TYRE_REPAIR("Tyre Repair"),
    TYRE_MOUNT("Tyre Mount"),
    TRAILER("Trailer"),
    TRAILER_MOUNT("Trailer Mount"),
    TYRE_VENDORS("Tyre Vendors"),
    TYRE_WORN_OUT_ALERTS("Tyre Worn Out alerts");



    override fun toString(): String {
        return value
    }
}