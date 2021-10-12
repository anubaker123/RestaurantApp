package com.example.alltrailsrestaurantapp.util

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ErrorMessageWrapper (val stringResourceId: Int, val suffixString: String? = "") {
    fun buildMessage(context: Context?): String {
        val resultSuffixString = if(suffixString ?: "" != "" && suffixString != "No value") ":\n$suffixString" else ""
        val defaultString = "An unknown error occurred (couldn't get activity context to display error)"
        return (context?.getString(stringResourceId) ?: defaultString) + resultSuffixString
    }
}

fun <T> Flow<T>.handleErrors(stringResourceId: Int,
                             errorLiveData: MutableLiveData<ErrorMessageWrapper>
): Flow<T> =
        catch {
            errorLiveData.postValue(ErrorMessageWrapper(stringResourceId, it.message))
        }

//fun <T> Flow<T>.handleErrors(stringResourceId: Int,
//                             errorLiveData: MutableLiveData<ErrorMessageWrapper>,
//                             postCatch: () -> Unit
//): Flow<T> =
//        catch {
//            errorLiveData.postValue(ErrorMessageWrapper(stringResourceId, it.message))
//            postCatch()
//        }
//
//suspend fun <T> handleErrors(stringResourceId: Int,
//                             errorLiveData: MutableLiveData<ErrorMessageWrapper>,
//                             funToHandle: suspend () -> T
//) {
//    try {
//        funToHandle()
//    } catch (e: Exception) {
//        errorLiveData.postValue(ErrorMessageWrapper(stringResourceId, e.message))
//    }
//}