package com.nabeel.udhaarkhata.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val selected = MutableLiveData<String>()

    private fun loadUsers(selected:String) {
        this.selected.value=selected;
        // Do an asynchronous operation to fetch users.
    }
}