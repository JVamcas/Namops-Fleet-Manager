package com.pet001kambala.namopsfleetmanager.repository

import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pet001kambala.namopsfleetmanager.model.AbstractModel
import com.pet001kambala.namopsfleetmanager.model.Account
import com.pet001kambala.namopsfleetmanager.model.PhoneAuthCred
import com.pet001kambala.namopsfleetmanager.utils.Docs
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion._toPhone
import com.pet001kambala.namopsfleetmanager.utils.ParseUtil.Companion.toMap
import com.pet001kambala.namopsfleetmanager.utils.Results
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AccountRepo {

    private val DB = FirebaseFirestore.getInstance()
    private val AUTH = Firebase.auth

    suspend fun createNewUserWithEmailAndPassword(
        account: Account,
        password: String
    ): Results {
        return try {
            AUTH.createUserWithEmailAndPassword(account.email!!, password).await()
            val accountId = Firebase.auth.currentUser?.uid
            account.id = accountId!!
            DB.collection(Docs.ACCOUNTS.name).document(account.id).set(account).await()
            Results.Success<Account>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    suspend fun sendVerificationEmail(): Results {
        val user = Firebase.auth.currentUser
        return try {
            user?.sendEmailVerification()?.await()
            Results.Success<Account>(code = Results.Success.CODE.VERIFICATION_EMAIL_SENT)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun listenForAuthChange(): Flow<Results> = callbackFlow {
        //listen for auth changes
        val mAuthListener = FirebaseAuth.AuthStateListener { auth ->
            //if auth.currentUser is not null
            auth.currentUser?.let {
                when {
                    isEmailAuth() && !it.isEmailVerified -> offer(Results.Error(AbstractModel.EmailNotVerifiedException()))
                    else -> {
                        offer(Results.Success<Account>(code = Results.Success.CODE.AUTH_SUCCESS))
                    }
                }
                return@AuthStateListener
            }
            //user is not authenticated at all
            offer(Results.Error(AbstractModel.NoAuthException()))
        }
        Firebase.auth.addAuthStateListener(mAuthListener)
        awaitClose { Firebase.auth.removeAuthStateListener(mAuthListener) }
    }

    suspend fun authenticateWithEmailAndPassword(
        email: String,
        password: String
    ): Results {
        return try {
            AUTH.signInWithEmailAndPassword(email, password).await()
            Results.Success<Account>(code = Results.Success.CODE.AUTH_SUCCESS)
        } catch (e: java.lang.Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException ->
                    Results.Error(AbstractModel.InvalidPasswordEmailException())
                else -> Results.Error(e)
            }
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun accountChangeListener(userId: String): Flow<Results> = callbackFlow {

        val accountRef = DB.collection(Docs.ACCOUNTS.name).document(userId)
        try {//1. first load the account data
            val shot = accountRef.get().await()
            val data = if (shot.exists())
                arrayListOf(shot.toObject(Account::class.java)!!)
            else ArrayList()

            offer(
                Results.Success<Account>(
                    data = data,
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
            )
        } catch (e: Exception) {
            offer(Results.Error(e))
        }
        //2.  then listen for document changes on the tyre collection
        val subscription = accountRef.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                val data = if (exists())
                    arrayListOf(toObject(Account::class.java)!!)
                else ArrayList()
                val results = Results.Success(
                    data = ArrayList(data),
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
                offer(results)
            }
        }
        awaitClose { subscription.remove() }
    }


    suspend fun resetPassword(email: String): Results {
        return try {
            Firebase.auth.sendPasswordResetEmail(email).await()
            Results.Success<Account>(code = Results.Success.CODE.PASSWORD_RESET_LINK_SENT)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Results {
        return try {
            AUTH.signInWithCredential(credential).await()
            Results.Success<Account>(code = Results.Success.CODE.AUTH_SUCCESS)
        } catch (e: java.lang.Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> Results.Error(AbstractModel.InvalidPhoneAuthCodeException())
                else -> Results.Error(e)
            }
        }
    }

    suspend fun createUserWithPhone(account: Account): Results {
        account.let {
            it.id = AUTH.currentUser?.uid ?: ""
            it.cellphone = it.cellphone!!._toPhone()
        }

        return try {
            DB.collection(Docs.ACCOUNTS.name).document(account.id).set(account).await()
            Results.Success<Account>(code = Results.Success.CODE.WRITE_SUCCESS)
        } catch (e: java.lang.Exception) {
            Results.Error(e)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun verifyPhoneNumber(
        phone: String,
        activity: FragmentActivity
    ): Flow<Results> = callbackFlow {
        offer(Results.loading())
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phone._toPhone())// Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS)// Timeout duration
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    offer(
                        Results.Success(
                            data = arrayListOf(PhoneAuthCred(phoneAuthCredential = p0)),
                            code = Results.Success.CODE.PHONE_VERIFY_SUCCESS
                        )
                    )
                    cancel()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    offer(Results.Error(p0))
                    cancel()
                }

                override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, p1)
                    offer(Results.Success(
                        data = arrayListOf(PhoneAuthCred(verificationId = verificationId )),
                        code = Results.Success.CODE.PHONE_VERIFY_CODE_SENT))
                }

                // close channel if code expired
                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    offer(Results.Error(AbstractModel.PhoneVerificationCodeExpired()))
                    cancel()
                }
            }) // OnVerificationStateChangedCallbacks
            .setActivity(activity)// Activity (for callback binding)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose { }
    }

    private fun isEmailAuth(): Boolean {
        val user = Firebase.auth.currentUser
        return user?.providerData?.get(1)?.providerId == EmailAuthProvider.PROVIDER_ID
    }

    fun updateAccountDetails(account: Account): Results {
        return try {
            DB.collection(Docs.ACCOUNTS.name)
                .document(account.id)
                .update(
                    account.toMap()
                )
            Results.Success<Account>(code = Results.Success.CODE.UPDATE_SUCCESS)
        }
        catch (e: java.lang.Exception){
            Results.Error(e)
        }
    }
    @ExperimentalCoroutinesApi
    fun accountsChangeListener(): Flow<Results> = callbackFlow {
        val collection = DB.collection(Docs.ACCOUNTS.name)
        //1. first load the TyreSurveyItem data
        offer(loadAccounts())
        //2.  then listen for document changes on the [TyreSurveyItem] collection
        val subscription = collection.addSnapshotListener { shot, error ->
            error?.let {
                offer(Results.Error(error))
            }
            shot?.apply {
                val data =
                    if (!this.isEmpty)
                        ArrayList(shot.documents.mapNotNull { it.toObject(Account::class.java) })
                    else null

                val results = Results.Success(
                    data = data,
                    code = Results.Success.CODE.LOAD_SUCCESS
                )
                offer(results)
            }
        }
        awaitClose { subscription.remove() }
    }

    private suspend fun loadAccounts(): Results {
        val collection = DB.collection(Docs.ACCOUNTS.name)
        return try {
            val shot = collection.get().await()
            val data = shot.documents.mapNotNull { it.toObject(Account::class.java) }
            Results.Success<Account>(
                data = ArrayList(data),
                code = Results.Success.CODE.LOAD_SUCCESS
            )
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

    suspend fun updateUserPermissions(account: Account): Results {

        return try {
            DB.collection(Docs.ACCOUNTS.name).document(account.id).update(account.toMap()).await()
            Results.Success<Account>(code = Results.Success.CODE.UPDATE_SUCCESS)
        }
        catch (e: java.lang.Exception){
            Results.Error(e)
        }
    }
}