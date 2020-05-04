package ru.adonixis.vkcupdocs.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.adonixis.vkcupdocs.models.VKDoc
import ru.adonixis.vkcupdocs.requests.VKDocRemoveRequest
import ru.adonixis.vkcupdocs.requests.VKDocRenameRequest
import ru.adonixis.vkcupdocs.requests.VKDocsRequest

class DocsViewModel : ViewModel() {
    companion object {
        private const val TAG = "DocsViewModel"
    }
    private var docsLiveData: MutableLiveData<List<VKDoc>>? = null
    private var removeDocLiveData: MutableLiveData<Int>? = null
    private var renameDocLiveData: MutableLiveData<Int>? = null
    private var errorMessageLiveData: MutableLiveData<String>? = null

    fun getDocsLiveData(): LiveData<List<VKDoc>> {
        docsLiveData = MutableLiveData()
        return docsLiveData as LiveData<List<VKDoc>>
    }

    fun getRemoveDocLiveData(): LiveData<Int> {
        removeDocLiveData = MutableLiveData()
        return removeDocLiveData as LiveData<Int>
    }

    fun getRenameDocLiveData(): LiveData<Int> {
        renameDocLiveData = MutableLiveData()
        return renameDocLiveData as LiveData<Int>
    }

    fun getErrorMessageLiveData(): LiveData<String> {
        errorMessageLiveData = MutableLiveData()
        return errorMessageLiveData as LiveData<String>
    }

    fun getDocs(offset: Int = 0) {
        VK.execute(VKDocsRequest(offset), object: VKApiCallback<List<VKDoc>> {
            override fun success(result: List<VKDoc>) {
                docsLiveData?.value = result
            }

            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
                errorMessageLiveData!!.value = error.toString()
            }
        })
    }

    fun removeDoc(ownerId: Int, docId: Int) {
        VK.execute(VKDocRemoveRequest(ownerId, docId), object: VKApiCallback<Int> {
            override fun success(result: Int) {
                if (result == 1) {
                    removeDocLiveData?.value = result
                } else {
                    errorMessageLiveData!!.value = result.toString()
                }
            }

            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
                errorMessageLiveData!!.value = error.toString()
            }
        })
    }

    fun renameDoc(ownerId: Int, docId: Int, title: String) {
        VK.execute(VKDocRenameRequest(ownerId, docId, title), object: VKApiCallback<Int> {
            override fun success(result: Int) {
                if (result == 1) {
                    renameDocLiveData?.value = result
                } else {
                    errorMessageLiveData!!.value = result.toString()
                }
            }

            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
                errorMessageLiveData!!.value = error.toString()
            }
        })
    }

}