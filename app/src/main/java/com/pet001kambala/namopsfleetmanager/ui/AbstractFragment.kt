package com.pet001kambala.namopsfleetmanager.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.datepicker.MaterialDatePicker
import com.pet001kambala.namopsfleetmanager.R
import com.pet001kambala.namopsfleetmanager.databinding.ProgressbarBinding
import com.pet001kambala.namopsfleetmanager.databinding.WarningDialogBinding
import com.pet001kambala.namopsfleetmanager.model.AbstractModel
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.ui.account.AccountViewModel
import com.pet001kambala.namopsfleetmanager.ui.account.auth.AbstractAuthFragment
import com.pet001kambala.namopsfleetmanager.utils.AccessType
import com.pet001kambala.namopsfleetmanager.utils.DateUtil
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.isAuthorized
import com.pet001kambala.namopsfleetmanager.utils.Results
import com.pet001kambala.namopsfleetmanager.utils.Results.Error.CODE.*
import com.pet001kambala.namopsfleetmanager.utils.Results.Success.CODE.*
import jxl.Workbook
import kotlinx.coroutines.ExperimentalCoroutinesApi
import lib.folderpicker.FolderPicker
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractFragment : Fragment() {

    private var mDialog: Dialog? = null
    lateinit var navController: NavController
    private lateinit var mProgressbarBinding: ProgressbarBinding
    private val accountModel: AccountViewModel by activityViewModels()
    val ERR_NO_AUTH: String = "Err: Not authorized to "

    var currentAccount: Account? = null


    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        currentAccount = accountModel.currentAccount.value
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackClicks()

        accountModel.authState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it != AccountViewModel.AuthState.AUTHENTICATED
                    && this@AbstractFragment !is AbstractAuthFragment
                )
                    navController.navigate(R.id.action_global_authentication)
            }
        })
    }


    fun isAuthorized(accessType: AccessType): Boolean {
        return currentAccount.isAuthorized(accessType)
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_DIR) {
                data?.apply {
                    val folderLocation: String? = extras?.getString("data")
                    callback(folderLocation)
                }
            }
        }
    }

    protected open fun showProgressBar(message: String) {
        if (mDialog == null || mDialog?.isShowing == false) {
            mProgressbarBinding = ProgressbarBinding.inflate(layoutInflater, null, false)
            mProgressbarBinding.progressMsg.text = message

            mDialog = Dialog(requireContext(), android.R.style.Theme_Black)
            mDialog?.apply {

                window?.setGravity(Gravity.BOTTOM)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawableResource(R.color.transparent)
                setContentView(mProgressbarBinding.root)
                setCancelable(false)
                show()
            }
        }
    }


    protected fun updateProgressBarMsg(msg: String) {
        mProgressbarBinding.progressMsg.text = msg
    }

    protected fun setAdapter(view: AutoCompleteTextView, list: List<String>) {
        view.setAdapter(
            ArrayAdapter(requireContext(), R.layout.auto_complete_text_layout, list)
        )
    }

    private lateinit var callback: (String?) -> Unit
    private val SELECT_DIR = 0
    private fun getStorageDir(callback: (path: String?) -> Unit) {
        this.callback = callback

        val intent = Intent(requireContext(), FolderPicker::class.java)
        intent.putExtra("title", "Select folder");
        startActivityForResult(intent, SELECT_DIR)
    }

    protected fun toStorage(
        fileName: String,
        sheetNameList: ArrayList<String>,
        sheetList: ArrayList<List<AbstractModel>>
    ) {
        if (sheetList.all { it.isNullOrEmpty() }) {
            showToast("No data to export.")
            return
        }
        getStorageDir { path ->
            path?.let {
                val file = File(path, fileName)
                val export = ParseUtil.toExcelSpreadSheet(
                    Workbook.createWorkbook(file),
                    sheetList,
                    sheetNameList
                )
                if (export)
                    showToast("Saved to ${file.absoluteFile}")
            }
        }
    }

    protected fun selectDate(callback: (date: String) -> Unit) {
        MaterialDatePicker.Builder.datePicker().apply {
            setSelection(Calendar.getInstance().timeInMillis)
            val picker = build()
            picker.show(requireActivity().supportFragmentManager, "")
            picker.addOnPositiveButtonClickListener { callback(DateUtil.fromLong(it)) }
        }
    }

    protected open fun endProgressBar() {
        mDialog?.cancel()
    }

    private fun handleBackClicks() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackClick()
                }
            })
    }

    protected open fun onBackClick() {
        navController.popBackStack()
    }

    fun showToast(msg: String?) {
        context?.let {
            Toast.makeText(it, msg, Toast.LENGTH_LONG).show()
        }
    }

    protected fun showWarningDialog(warningTxt: String?, mListener: WarningDialogListener) {
        with(WarningDialogBinding.inflate(layoutInflater, null, false))
        {
            val dialog = AlertDialog.Builder(requireContext()).let {
                it.setView(root)
                it.create()
            }.apply { show() }
            title.text = warningTxt
            val okBtnClicked = AtomicBoolean(false)
            okBtn.setOnClickListener {
                okBtnClicked.set(true)
                dialog.dismiss()
                mListener.onOkWarning()
            }
            cancelBtn.setOnClickListener { dialog.dismiss() }
            dialog.setOnDismissListener { if (!okBtnClicked.get()) mListener.onCancelWarning() }
        }
    }

    protected fun parseRepoResults(mResults: Results?) {
        if (mResults is Results.Success<*>) {
            when (mResults.code) {
                AUTH_SUCCESS -> {
                }
                WRITE_SUCCESS -> showToast(" Registration success.")
                UPDATE_SUCCESS -> showToast("Updated success.")
                LOGOUT_SUCCESS -> showToast("Logout successfully!")
                DELETE_SUCCESS -> showToast("Deleted successfully.")
                VERIFICATION_EMAIL_SENT -> showToast("Verification email sent.")
            }
        } else if (mResults is Results.Error) {
            when (mResults.code) {
                PERMISSION_DENIED -> showToast("Err: Permission denied!")
                NETWORK -> showToast("Err: No internet connection!")
                ENTITY_EXISTS -> showToast("Err: Duplicate.")
                AUTH -> showToast("Err: Authentication.")
                NO_RECORD -> showToast("Err: No record found for your search.")
                NO_ACCOUNT -> showToast("Err: No record found for your search.")
                NO_SUCH_USER -> showToast("Err: No account with such email.")
                DUPLICATE_ACCOUNT -> showToast("Err: Account already exist.")
                INCORRECT_EMAIL_PASSWORD_COMBO -> showToast("Err: Incorrect email or password.")
                INVALID_AUTH_CODE -> showToast("Err: Incorrect authentication code.")
                PHONE_VERIFICATION_CODE_EXPIRED -> showToast("Err: Verification code expired.")
                else -> showToast("Err: Unknown error!")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        endProgressBar()
    }

    interface WarningDialogListener {
        /***
         * Called when user Ok the delete Op
         */
        fun onOkWarning()

        /***
         * Called when user explicitly cancelled the op or when dialog dismissed
         * by touching elsewhere in the device screen
         */
        fun onCancelWarning()
    }
}