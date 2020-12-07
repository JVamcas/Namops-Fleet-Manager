package com.pet001kambala.namopsfleetmanager.utils

import android.content.Context
import android.text.TextUtils
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.Exclude
import com.google.gson.Gson
import com.pet001kambala.namopsfleetmanager.model.AbstractModel
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.utils.DateUtil.Companion._24
import com.squareup.picasso.Picasso
import jxl.write.Label
import jxl.write.WritableWorkbook
import java.io.File
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern

class ParseUtil {

    companion object {
        fun path(vararg param: String): String {
            val path = StringBuilder()

            for (p in param)
                path.append(p).append("/")
            return path.toString()
        }

        inline fun <reified O : AbstractModel?> copyOf(): O {
            val json = this.toJson()
            return json.convert()
        }

        fun String?.isValidUnitNo(): Boolean {
            return this.isValidTrailerNo() || this.isValidVehicleNo()
        }

        fun String?.isValidVehicleNo(): Boolean {
            val pattern = Pattern.compile("^[HL]\\d{2,}$")
            return !this.isNullOrEmpty() && pattern.matcher(this).matches()
        }

        fun String?.isValidTrailerNo(): Boolean {
            val pattern = Pattern.compile("^[TL]\\d{2,}$")
            return !this.isNullOrEmpty() && pattern.matcher(this).matches()
        }

        fun String?.isValidPlateNo(): Boolean {
            val pattern = Pattern.compile("^N\\d+[A-Z]+$")
            return !this.isNullOrEmpty() && pattern.matcher(this).matches()
        }

        fun <T> T.toMap(): Map<String, Any> {
            return convert()
        }

        @JvmStatic
        fun Tyre.mountDetails(): String {
            return if (mounted)
                "[Mounted on: ${trailerNo ?: horseNo}] [Pos = $mountPosition]"
            else "Location = $location"
        }

        inline fun <I, reified O> I.convert(): O {
            val json = this.toJson()
            return Gson().fromJson(json, object : TypeToken<O>() {}.type)
        }

        inline fun <reified O> String.convert(): O {
            return Gson().fromJson(this, object : TypeToken<O>() {}.type)
        }

        fun <K> K.toJson(): String {
            return Gson().toJson(this)
        }

        fun findFilePath(mContext: Context, filePath: String): String? {
            val file = File(mContext.getExternalFilesDir(null), filePath)
            return if (file.exists()) "file:" + file.absolutePath else null
        }

        fun yearOfMake(): List<String> {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            val year = calendar.get(Calendar.YEAR)
            return (year downTo 1990).map { it.toString() }
        }

        @Exclude
        fun Account?.isAuthorized(accessType: AccessType) =
            this != null && (permissionList.contains(accessType.name) || //is authorised
                    permissionList.contains(AccessType.ADMIN.name)) //or is admin

        /***
         * Write data to sheet in the given workbook
         * @param wkb the workbook
         * @param sheetList array of AbstractModel(s) each with headers and data to write in row
         * @param sheetNameList sheet to create and write to
         */
        fun toExcelSpreadSheet(
            wkb: WritableWorkbook,
            sheetList: ArrayList<List<AbstractModel>>,
            sheetNameList: ArrayList<String>
        ): Boolean {

            try {

                sheetList.withIndex().forEach {
                    val sheetData = it.value //
                    if (sheetData.isNotEmpty()) {
                        wkb.createSheet(sheetNameList[it.index], it.index).apply {
                            var colIndex = 0
                            var rowIndex = 0
                            val colHeaders = sheetData[0].data().map { it.first }
                            colHeaders.forEach { addCell(Label(colIndex++, rowIndex, it)) }

                            sheetData.forEach { model ->
                                rowIndex++
                                colIndex = 0
                                model.data().forEach {
                                    addCell(Label(colIndex++, rowIndex, it.second))
                                }
                            }
                        }
                    }
                }
                wkb.write()
                wkb.close()
                return true
            } catch (e: Exception) {
                return false
            }
        }


        fun initDirs(mContext: Context, vararg param: String) {
            //create dir for storing profile pictures
            for (dir in param) {
                val parentDir = File(mContext.getExternalFilesDir(null), dir)
                if (!parentDir.exists()) parentDir.mkdirs()
            }
        }

        /***
         * Compute relative path for the view's icon
         * @param rtDir the base dir for the icon
         * @param viewId id of the view
         * @return the relative path
         */
        fun iconPath(rtDir: String?, viewId: String): String {
            return StringBuilder(rtDir ?: "").append("/_").append(viewId).append("_.jpg")
                .toString()
        }

        /***
         * Test whether a given string is money
         * @param moneyString the string
         * @return true if it is else false
         */
        fun isMoney(moneyString: String?): Boolean {
            val MONEY_STRING = "^NAD[0-9]+(\\.[0-9]{1,2})?$"
            return Pattern.compile(MONEY_STRING).matcher(moneyString).matches()
        }

        /**
         * Format a double as money in the form <CURRENCY VALUE>
         *
         * @param currency the currency for the money
         * @param value    the double to be formatted e.g 6.90
         * @return the formatted value e.g NAD 6.90
        </CURRENCY> */
        fun moneyFormat(currency: String = "", value: Double): String {
            return currency + " " + DecimalFormat("0.00").format(value)
        }

        /***
         * Extract the double value from a string representation of money
         * @param moneyString string e.g NAD 34.90
         * @return double value of the string representation e.g 34.90 if non-null else 0.00
         */
        fun extractValueFromMoneyString(moneyString: String?): String {
            return if (moneyString.isNullOrEmpty())
                "0.00" else {
                val value = moneyString.replace("[^.0123456789]".toRegex(), "")
                if (value.isEmpty()) "0.00" else value
            }
        }

        /***
         * Get the prefix of tyre serial number
         */
        fun getSNPrefix(): String {
            val dat = DateUtil.today()._24()
            val match = dat.let { Regex("(\\d{2})(\\d{2})-(\\d{2})").find(it) }!!
            val (_, year, month) = match.destructured
            return "$year$month" + "NOL"
        }

        fun String?.stripCountryCode(): String? {
            return if (this == null)
                null
            else {
                val match = Regex("^(\\+264)?(\\d+)?(8[1,5]\\d+)$").find(this)
                val (_, _,cell) = match!!.destructured
                "0$cell"
            }
        }

        @JvmStatic
        fun String?.isValidTyreSN(): Boolean {

            val validSN = "^\\d{2}\\d{2}NOL[0-9]+$"
            return !TextUtils.isEmpty(this) && Pattern.matches(validSN, this)
        }

        fun isValidEmail(email: String?): Boolean {
            if (email.isNullOrEmpty()) return false
            val email1 = email.replace("\\s+".toRegex(), "")
            val EMAIL_STRING = ("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            return !TextUtils.isEmpty(email1) && Pattern.compile(EMAIL_STRING)
                .matcher(email1).matches()
        }

        fun isValidMobile(phone: String?): Boolean {
            if (phone.isNullOrEmpty()) return false
            val phone1 = phone.replace("\\s+".toRegex(), "")
            return phone1.isNotEmpty() && Pattern.matches(
                "^0?8[1,5]\\d{7}",
                phone1
            )
        }

        @JvmStatic
        fun String?.toPhone(): String {

            return "+264${this?.trimStart { it == '0' }}"
        }

        fun Account.isIncompleteAccount() =
            name.isNullOrEmpty()
                    || companyName.isNullOrEmpty()
                    || companyNumber.isNullOrEmpty()

        fun isValidAuthCode(code: String?): Boolean {
            return code?.length ?: 0 == 6
        }

        fun refreshImage(context: Context, rtDir: String, viewId: String) {
            val filePath = ParseUtil.iconPath(rtDir, viewId)
            val absPath = ParseUtil.findFilePath(context, filePath)
            Picasso.get().invalidate(absPath)
        }
    }
}