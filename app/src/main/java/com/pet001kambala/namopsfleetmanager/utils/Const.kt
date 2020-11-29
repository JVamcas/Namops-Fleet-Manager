package com.pet001kambala.namopsfleetmanager.utils

internal interface Const {
    companion object {
        val NEW_ACCOUNT: String = "new account"
        val PHONE_NUMBER: String = "phone number"
        val TYRE_VENDOR: String = "tyre_vendor"
        val TYRE_REPAIR: String = "tyre_repair"
        val ROW_POS: String = "row_pos"
        val SQUARE: String = "square"
        val VEHICLE: String = "vehicle"
        val ACCOUNT: String = "account"
        val TYRE: String = "tyre"
        val defaultLocation: String = "Storage"

        //constant for activity results
        val RC_SIGN_IN = 0
        val CAPTURE_PICTURE = 1
        val SCAN_BARCODE = 2

        val IMAGE_ROOT_PATH = "images"


        val TEMP_FILE = "temp"//only used when creating a new model


        val DISCARD_CHANGES = "Discard Changes?"

    }

}