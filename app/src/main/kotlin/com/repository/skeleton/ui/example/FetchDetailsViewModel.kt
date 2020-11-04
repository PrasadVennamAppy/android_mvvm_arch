package com.repository.skeleton.ui.example

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.repository.skeleton.R
import com.repository.skeleton.adapter.ViewType
import com.repository.skeleton.data.remote.example.FetchDetailsRepo
import com.repository.skeleton.data.remote.model.Resource
import com.repository.skeleton.data.remote.model.example.DogDetailsResponse
import com.repository.skeleton.livedata.SingleLiveEvent
import com.repository.skeleton.manager.CoroutinesManager
import com.repository.skeleton.utils.ResourceProvider
import kotlinx.coroutines.*

class FetchDetailsViewModel(
    private val resourceProvider: ResourceProvider,
    private val coroutinesManager: CoroutinesManager,
    private val fetchDetailsRepo: FetchDetailsRepo
) : ViewModel(), OnItemActionListener {

    private val list = ArrayList<ViewType<*>>()

    val insertEvent = SingleLiveEvent<Pair<Int, ViewType<*>>>()

    val removeItemEvent = SingleLiveEvent<Int>()

    val updateEvent = SingleLiveEvent<String>()
    val updateAPIEvent = SingleLiveEvent<ArrayList<DogDetailsResponse>>()
    val textObservable = ObservableField<String>()

    companion object {
        private const val logTag = "CoroutinesSample"
    }


    fun getList(): List<ViewType<*>> {
        list.clear()
        repeat(10) {
            list.add(SingleItemType(SingleItem("Dummy Text $it")))
        }

        return list
    }

    val dogList = ArrayList<DogDetailsResponse>()
    fun makeNetworkCall() {
        Log.i(logTag, "Set TextView using DataBinding")
        textObservable.set(resourceProvider.getString(R.string.info_txt))

        coroutinesManager.ioScope.launch {
            val deferredList = ArrayList<Deferred<*>>()

            Log.i(logTag, "Make 10 concurrent network calls")
            for (i in 0..18) {
                deferredList.add(async {
                    Log.i(logTag, "Network Call ID: $i")
                    fetchDetailsRepo.fetchDetails()
                })
            }

            deferredList.joinAll()
            val result = deferredList.awaitAll()
            for (i in result.indices) {
                val s = result[i] as Resource<*>
                if (s.status == Resource.Status.SUCCESS) {
                    dogList.add(s.data as DogDetailsResponse)
                }
            }
            Log.i(logTag, "All Networks calls complete")

            updateAPIEvent.postValue(dogList)
            Log.i(logTag, "Update UI")
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    override fun onItemClicked(item: SingleItem) {
        updateEvent.value = item.name
    }

}

interface OnItemActionListener {
    fun onItemClicked(item: SingleItem)
}

data class SingleItemType(private val model: SingleItem) :
    ViewType<SingleItem> {

    override fun layoutId(): Int = R.layout.list_item

    override fun data(): SingleItem = model
}

data class SingleItem(
    val name: String
)
