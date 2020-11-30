package com.pet001kambala.namopsfleetmanager.utils

enum class AccessType(var value: String) {

    //    Vehicles permission group

    VIEW_VEHICLES("View vehicle details."),
    REG_VEHICLE("Register new vehicle."),
    UPDATE_VEHICLE("Update vehicle details."),


    // Trailer permission group
    VIEW_TRAILER("View trailer details."),
    REG_TRAILER("Register new trailer."),
    UPDATE_TRAILER("Update trailer details."),


    //  Vendor permission group
    VIEW_TYRE_VENDOR("View tyre vendor details"),
    REG_TYRE_VENDOR("Register new tyre vendor."),
    UPDATE_TYRE_VENDOR("Update tyre vendor details."),


    //    Tyres permission group
    REG_TYRE("Register new tyre."),
    UPDATE_TYRE("Update tyre details"),
    VIEW_TYRE("View tyre details."),
    MOUNT_TYRE_OR_UNMOUNT_TYRE("Mount or unMount tyre."),
    INSPECT_TYRE("Carry out tyre inspections."),
    SEND_OR_RECEIVE_TYRE_FROM_VENDOR("Send or receive tyre from tyre vendor."),

    ADMIN("Make an admin on this app.");

    override fun toString(): String {
        return value
    }
}