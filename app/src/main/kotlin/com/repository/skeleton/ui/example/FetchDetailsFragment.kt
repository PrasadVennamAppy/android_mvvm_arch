package com.repository.skeleton.ui.example

import android.os.Bundle
import androidx.lifecycle.Observer
import com.repository.skeleton.R
import com.repository.skeleton.adapter.ViewType
import com.repository.skeleton.adapter.ViewTypeAdapter
import com.repository.skeleton.base.DataBindingFragment
import com.repository.skeleton.data.remote.model.example.DogDetailsResponse
import com.repository.skeleton.databinding.FragmentFetchDetailsBinding
import com.repository.skeleton.extensions.lazyN
import com.repository.skeleton.extensions.toast
import org.koin.android.ext.android.inject


class FetchDetailsFragment : DataBindingFragment<FragmentFetchDetailsBinding>() {

    private val vm by inject<FetchDetailsViewModel>()
    private val adapter by lazyN { ViewTypeAdapter<ViewType<*>>(onItemActionListener = vm) }
    companion object {
        const val TAG  = "FetchDogsFragment"
        fun newInstance() =
            FetchDetailsFragment()
    }

    override fun layoutId() = R.layout.fragment_fetch_details

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vb.vm = vm
        adapter.setList(vm.getList())
        vm.makeNetworkCall()
        vb.res.adapter = adapter
        initObservers()
    }

    private fun initObservers() {
        vm.updateEvent.observe(viewLifecycleOwner, Observer {
            toast(it)
        })
        vm.updateAPIEvent.observe(viewLifecycleOwner, Observer {
            /*parallel Network Calls*/
            updateUI(it)
        })
    }

    private fun updateUI(arrayList: ArrayList<DogDetailsResponse>) {
        val sb = StringBuilder()
        repeat(arrayList.size) {
            sb.append(arrayList[it].message + System.getProperty("line.separator"))
        }
        vb.textView.text = sb.toString()
    }

}
