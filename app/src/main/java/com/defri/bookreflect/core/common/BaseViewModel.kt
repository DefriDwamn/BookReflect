package com.defri.bookreflect.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Event> : ViewModel() {
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<State> = _state.asStateFlow()

    abstract fun initialState(): State
    abstract fun handleEvent(event: Event)

    fun setState(reduce: State.() -> State) {
        val newState = state.value.reduce()
        _state.value = newState
    }

    protected fun launchWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 