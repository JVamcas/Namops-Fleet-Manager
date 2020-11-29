package com.pet001kambala.namopsfleetmanager.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.pet001kambala.namopsfleetmanager.model.AuthType
import com.pet001kambala.namopsfleetmanager.model.Tyre
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.extractDigit
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.getSNPrefix
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isMoney
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isValidEmail
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isValidMobile
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isValidTyreSN
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isValidUnitNo
import com.skydoves.powerspinner.PowerSpinnerView
import com.squareup.picasso.Picasso
import java.util.regex.Pattern

class BindingUtil {

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["errorMsg", "editContent"])
        fun emptyEdit(mEditText: EditText, errorMsg: String?, value: String?) {
            mEditText.error = if (value.isNullOrEmpty()) errorMsg
            else null
        }

        @JvmStatic
        @BindingAdapter(value = ["errorMsg", "idMailCell"])
        fun validateIDMailCell(
            mEditText: TextInputEditText,
            errorMsg: String?,
            idMailCell: String?
        ) {

            mEditText.error =
                if (idMailCell.isNullOrEmpty() || isValidMobile(idMailCell) || isValidEmail(
                        idMailCell
                    )
                )
                    null else errorMsg
        }

        @JvmStatic
        @BindingAdapter(value = ["password"])
        fun validatePassword(mEditText: EditText, password: String?) {
            mEditText.error =
                if (password.isNullOrEmpty() || password.length >= 8) null else
                    "Password should be at least 8 characters long."
        }

        @JvmStatic
        @BindingAdapter(value = ["password", "confirmPassword"])
        fun confirmPassword(mEditText: EditText, password: String?, confirmPassword: String?) {
            mEditText.error =
                if (confirmPassword.isNullOrEmpty() || password != confirmPassword) "Passwords do not match." else null
        }

        @JvmStatic
        @BindingAdapter(value = ["password", "idMailCell"], requireAll = false)
        fun isValidLogin(mButton: MaterialButton, password: String?, idMailCell: String?) {
            mButton.isEnabled =
                (password?.length ?: 0 >= 8 && isValidEmail(idMailCell) || isValidMobile(
                    idMailCell
                ) || idMailCell?.length ?: 0 == 11)
        }

        @JvmStatic
        @BindingAdapter(value = ["accountName", "emailAddress", "cellphone", "password", "confirmPassword", "authType", "companyName"])
        fun validateEmailRegistration(
            mButton: MaterialButton,
            accountName: String?,
            emailAddress: String?,
            cellphone: String?,
            password: String?,
            confirmPassword: String?,
            authType: String?,
            companyName: String?
        ) {
            mButton.isEnabled = isValidSelection(
                arrayListOf(
                    accountName,
                    authType,
                    companyName
                )
            ) && ((isValidMobile(cellphone) && authType == AuthType.PHONE.name) && (emailAddress.isNullOrEmpty() || isValidEmail(
                emailAddress
            ))
                    || (isValidEmail(emailAddress) && authType == AuthType.EMAIL.name) && (cellphone.isNullOrEmpty() || isValidMobile(
                cellphone
            )))
                    && (authType == AuthType.PHONE.name || password?.length ?: 0 >= 8 && confirmPassword == password)

        }

        @JvmStatic
        @BindingAdapter(value = ["emailAddress", "cellphone", "isEmail"])
        fun validateEmailCell(
            mEditText: TextInputEditText,
            emailAddress: String?,
            cellphone: String?,
            isEmail: Boolean = false
        ) {
            mEditText.error =
                if (isEmail) {
                    if (emailAddress.isNullOrEmpty() || !isValidEmail(emailAddress))
                        "Enter a valid email address."
                    else null
                } else if (cellphone.isNullOrEmpty() || !isValidMobile(cellphone))
                    "Enter valid phone number."
                else null
        }

        @JvmStatic
        @BindingAdapter(value = ["tyre_sn"])
        fun validateTyreSN(mEditText: EditText, tyre_sn: String?) {
            mEditText.error = if (tyre_sn.isValidTyreSN()) null else "Invalid tyre serial number."
        }

        @JvmStatic
        @BindingAdapter(value = ["errorMsg", "editContent"])
        fun validateSelectionMade(
            spinner: PowerSpinnerView,
            errorMsg: String?,
            editContent: String?
        ) {
            spinner.error = if (editContent.isNullOrEmpty() || editContent.contains("Select"))
                errorMsg else null
        }

        @JvmStatic
        @BindingAdapter(value = ["vendor_name", "vendor_phone_1", "vendor_address_1", "vendor_city", "vendor_email"])
        fun validateVendorRegistration(
            mButton: MaterialButton,
            vendor_name: String?,
            vendor_phone_1: String?,
            vendor_address_1: String?,
            vendor_city: String?,
            vendor_email: String?
        ) {
            mButton.isEnabled = isValidSelection(
                arrayListOf(
                    vendor_address_1,
                    vendor_city,
                    vendor_name,
                    vendor_phone_1
                )
            )
                    && (vendor_email.isNullOrEmpty() || isValidEmail(vendor_email))
        }


        @JvmStatic
        @BindingAdapter(value = ["unitNo", "trailerMake", "trailerPlate", "trailerDepartment"])
        fun validateTrailerRegistration(
            mButton: MaterialButton,
            unitNo: String?,
            trailerMake: String?,
            trailerPlate: String?,
            trailerDepartment: String?
        ) {
            mButton.isEnabled = isValidSelection(
                arrayListOf(
                    unitNo,
                    trailerDepartment,
                    trailerMake,
                    trailerPlate
                )
            )
        }

        @JvmStatic
        @BindingAdapter(value = ["serialNo", "repair_vendor", "repair_thread_depth", "repair_thread_type", "repair_cost", "sent_thread_depth", "sent_thread_type"])
        fun validateTyreRepair(
            mButton: MaterialButton,
            serialNo: String?,
            repair_vendor: String?,
            repair_thread_depth: String?,
            repair_thread_type: String?,
            repair_cost: String?,
            sent_thread_depth: String?,
            sent_thread_type: String?
        ) {
            mButton.isEnabled = serialNo.isValidTyreSN()
                    && isValidSelection(
                arrayListOf(
                    repair_thread_depth,
                    repair_vendor,
                    repair_thread_type,
                    sent_thread_depth,
                    sent_thread_type
                )
            ) && isMoney(repair_cost)
        }

        /***
         * Load image from provided url, transform it and set it into the imageview
         * @param mView the image view
         * @param default_icon default icon in case of error or when url is null
         * @param photoUrl the url: a base dir for device images else full url for online
         * @param size the required size of the image
         */
        @JvmStatic
        @BindingAdapter(value = ["viewId", "default_icon", "photoUrl", "size"])
        fun loadImage(
            mView: ImageView,
            viewId: String?,
            @IdRes default_icon: Int,
            photoUrl: String?,
            size: Int
        ) {
            val filePath = ParseUtil.iconPath(Const.IMAGE_ROOT_PATH, viewId ?: "")
            val absPath = ParseUtil.findFilePath(mView.context, filePath)

            Picasso.get().invalidate(absPath)

            val creator =
                ImageTransformer.ImageUtil.requestCreator(
                    ImageTransformer.CircleTransformation,
                    absPath,
                    size,
                    default_icon
                )
            creator.into(mView)
        }

        @JvmStatic
        @BindingAdapter(value = ["tyre_sn", "tyre"])
        fun validateNewTyreSN(mEditText: EditText, tyre_sn: String?, tyre: Tyre) {
            mEditText.addTextChangedListener(object : Companion.TextChangeLister() {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    super.onTextChanged(p0, p1, p2, p3)
                    val sn = p0.toString()
                    val prefix = getSNPrefix()
                    val pattern = Pattern.compile("^(${prefix})([0-9]+?)$")
                    val matcher = pattern.matcher(sn)

                    mEditText.error =
                        if (sn.isValidTyreSN()) null else "Invalid tyre serial number."

                    val index = if (matcher.matches()) matcher.group(2) else ""

                    tyre.serialNumber = prefix + index
                    mEditText.setSelection(sn.length)
                }
            })
        }

        @JvmStatic
        @BindingAdapter(value = ["tyre_purchase_price", "tyre"])
        fun formatPrice(mEditText: EditText, tyre_purchase_price: String?, tyre: Tyre) {
            mEditText.addTextChangedListener(object : Companion.TextChangeLister() {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    super.onTextChanged(p0, p1, p2, p3)
                    val moneyString = p0.toString()
                    val digits = extractDigit(moneyString)

                    mEditText.error =
                        if (isMoney(moneyString)) null else "Invalid price."

                    tyre.purchasePrice = "NAD$digits"
                    mEditText.setSelection(moneyString.length)
                }
            })
        }

        @JvmStatic
        @BindingAdapter(value = ["serialNo", "tyre_size", "tyre_brand", "purchase_condition", "purchase_date", "cost", "thread_type", "thread_depth", "recommended_pressure"])
        fun validateTyreRegistration(
            mButton: MaterialButton,
            serialNo: String?,
            tyre_size: String?,
            tyre_brand: String?,
            purchase_condition: String?,
            purchase_date: String?,
            cost: String?,
            thread_type: String?,
            thread_depth: String?,
            recommended_pressure: String?
        ) {
            mButton.isEnabled =
                serialNo.isValidTyreSN()
                        && isValidSelection(
                    arrayListOf(
                        tyre_brand,
                        tyre_size,
                        purchase_condition,
                        purchase_date,
                        thread_depth,
                        recommended_pressure,
                        thread_type
                    )
                )
                        && isMoney(cost)
        }

        private fun isValidSelection(list: ArrayList<String?>): Boolean {
            return !list.any { it.isNullOrEmpty() || it.contains("Select") }
        }

        @JvmStatic
        @BindingAdapter(value = ["errorMsg","unit_number"])
        fun validateUnitNo(mEditText: EditText,errorMsg: String?,unit_number: String?){
            mEditText.error = if(unit_number.isValidUnitNo()) null else errorMsg
        }
        @JvmStatic
        @BindingAdapter(value = ["serialNo", "mountPosition", "vehicleUnitNo", "un_mount_reason"])
        fun validateTyreMount(
            mButton: MaterialButton,
            serialNo: String?,
            mountPosition: String?,
            vehicleUnitNo: String?,
            un_mount_reason: String?
        ) {
            mButton.isEnabled =
                serialNo.isValidTyreSN()
                        && isValidSelection(
                    arrayListOf(
                        mountPosition,
                        un_mount_reason
                    )
                )
                        && vehicleUnitNo.isValidUnitNo()

        }


        @JvmStatic
        @BindingAdapter(value = ["vinNo", "unitNo", "vehicleType", "vehicleModel", "vehicleMake", "year"])
        fun validateVehicleRegistration(
            mButton: MaterialButton,
            vinNo: String?,
            unitNo: String?,
            vehicleType: String?,
            vehicleModel: String?,
            vehicleMake: String?,
            year: String?
        ) {
            mButton.isEnabled =
                isValidSelection(
                    arrayListOf(
                        vehicleMake,
                        vehicleModel,
                        vehicleType,
                        year,
                        vinNo
                    )
                )
                        && unitNo.isValidUnitNo()
        }

        @JvmStatic
        @BindingAdapter(value = ["plateNo", "vehicle_color", "engine_number", "department", "fuel_type"])
        fun validateVehicleContRegistration(
            mButton: MaterialButton,
            plateNo: String?,
            vehicle_color: String?,
            engine_number: String?,
            department: String?,
            fuel_type: String?
        ) {
            mButton.isEnabled =
                isValidSelection(
                    arrayListOf(
                        plateNo,
                        vehicle_color,
                        fuel_type,
                        department,
                        engine_number
                    )
                )
        }

        @JvmStatic
        @BindingAdapter(value = ["serialNo", "threadDepth", "currentCondition", "valve_condition", "actual_pressure", "current_thread_type"])
        fun validateTyreSurvey(
            mButton: MaterialButton,
            serialNo: String?,
            threadDepth: String?,
            currentCondition: String?,
            valveCondition: String?,
            actualPressure: String?,
            currentThreadType: String?
        ) {
            mButton.isEnabled =
                serialNo.isValidTyreSN()
                        && isValidSelection(
                    arrayListOf(
                        threadDepth,
                        currentCondition,
                        currentThreadType,
                        actualPressure,
                        valveCondition
                    )
                )
        }

        @JvmStatic
        @BindingAdapter(value = ["email_address"])
        fun validateEmail(mEditText: EditText, email_address: String?) {
            mEditText.error =
                if (!email_address.isNullOrEmpty() && !isValidEmail(email_address)) "Invalid Email address." else null
        }

        abstract class TextChangeLister : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }
    }
}