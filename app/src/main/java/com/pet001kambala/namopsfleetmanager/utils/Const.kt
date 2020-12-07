package com.pet001kambala.namopsfleetmanager.utils

internal interface Const {
    companion object {
        const val TYRE_WORN_OUT: String = "/topics/TyreWornOut"
        const val TYRE_WORNOUT_NOTIFICATION_CHANNEL: String = "Tyre worn-out alert"
        const val MODEL: String = "AbstractModel"
        const val MIN_TYRE_DEPTH: Int = 4
        const val MAX_TYRE: Int = 50
        const val PHONE_NUMBER: String = "phone number"
        const val TYRE_VENDOR: String = "tyre_vendor"
        const val TYRE_REPAIR: String = "tyre_repair"
        const val SQUARE: String = "square"
        const val VEHICLE: String = "vehicle"
        const val ACCOUNT: String = "account"
        const val TYRE: String = "tyre"
        const val defaultLocation: String = "Storage"
        val TRAILER = "trailer"

        //constant for activity results
        val RC_SIGN_IN = 0
        val CAPTURE_PICTURE = 1
        val SCAN_BARCODE = 2

        val IMAGE_ROOT_PATH = "images"


        val TEMP_FILE = "temp"//only used when creating a new model


        val DISCARD_CHANGES = "Discard Changes?"

    }
}