package com.acv.mvp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class Action
object LoadForm : Action()
data class FormLoaded(
    val name: String,
    val phone: String,
    val mail: String,
) : Action()

data class ChangeName(val name: String) : Action()
data class ChangePhone(val phone: String) : Action()
data class ChangeMail(val mail: String) : Action()

interface StoreState

data class FormState(
    val isLoading: Boolean,
    val name: String,
    val phone: String,
    val mail: String,
) : StoreState {
    companion object {
        fun empty() = FormState(isLoading = true, name = "", phone = "", mail = "")
    }
}
//sealed class FormState : StoreState {
//    object Error : FormState()
//    object Loading : FormState()
//    data class Success(
//        val name: String,
//        val phone: String,
//        val mail: String,
//    ) : FormState() {
//        companion object {
//            fun empty() = Success("", "", "")
//        }
//    }
//
//    fun state(f: (Success) -> FormState) =
//        if (this is Success) f(this)
//        else f(Success.empty())
//}


abstract class Store<A> : ViewModel() {
    abstract val state: StateFlow<A>
    abstract fun action(action: Action)
}

class Repository() {
    private val name = ""
    private val phone = ""
    private val mail = ""

    suspend fun getName(): String = name
    suspend fun getPhone(): String = phone
    suspend fun getMail(): String = mail

    val nameFlow: MutableStateFlow<String> = MutableStateFlow("")
    val phoneFlow: MutableStateFlow<String> = MutableStateFlow("")
    val mailFlow: MutableStateFlow<String> = MutableStateFlow("")
}

@OptIn(ExperimentalCoroutinesApi::class)
class FormStore(
    private val repository: Repository
) : Store<FormState>() {
    override val state: MutableStateFlow<FormState> = MutableStateFlow(FormState.empty())

    override fun action(action: Action) {
        state.value = action.reduce(state.value)
        action.sideEffects()
    }

    private fun Action.reduce(currentState: FormState): FormState =
        when (this) {
            is LoadForm -> currentState.copy(isLoading = true)
            is FormLoaded -> currentState.copy(isLoading = false, name = name, mail = mail, phone = phone)
            is ChangeName -> currentState.copy(name = name)
            is ChangeMail -> currentState.copy(mail = mail)
            is ChangePhone -> currentState.copy(phone = phone)
        }

    private fun Action.sideEffects() {
        when (this) {
            is LoadForm -> loadForm()
        }
    }

    private fun loadForm() {
        viewModelScope.launch {
            action(
                FormLoaded(
                    repository.getName(),
                    repository.getMail(),
                    repository.getPhone(),
                )
            )
        }
    }
}