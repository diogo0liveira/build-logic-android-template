package com.dao.android.template

import androidx.lifecycle.ViewModel
import com.dao.android.module.core.GetGreeting
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
internal class MainViewModel @Inject constructor(getGreeting: GetGreeting) : ViewModel() {
    val greeting: StateFlow<String>
        field: MutableStateFlow<String> = MutableStateFlow(getGreeting())
}
