package cz.helmisek.mvvmi.main

import android.graphics.Color
import cz.helmisek.mvvmilib.BaseAction
import cz.helmisek.mvvmilib.BaseViewModel
import cz.helmisek.mvvmi.main.model.MainUiModel
import cz.helmisek.mvvmi.main.model.modifyAge
import cz.helmisek.mvvmi.main.model.modifyFirstName
import cz.helmisek.mvvmi.main.model.modifyLastName
import cz.helmisek.mvvmilib.send
import cz.helmisek.mvvmilib.update

class MainViewModel : BaseViewModel() {

    val mainUiModelSubject by lazy { findModel(MainUiModel::class.java) }
    val actionChangeBackgroundColor by lazy { findAction(Action.ActionChangeBackgroundColor::class.java) }

    override fun setup() {
        addModel(MainUiModel::class.java, MainUiModel())
        addAction(Action.ActionChangeBackgroundColor::class.java)
    }

    fun notifyUiModelResult(result: Result) {
        when (result) {
            is Result.InputFirstNameResult -> mainUiModelSubject.update { it.modifyFirstName { result.value } }
            is Result.InputLastNameResult -> mainUiModelSubject.update { it.modifyLastName { result.value } }
            is Result.InputAgeResult -> mainUiModelSubject.update { it.modifyAge { result.value } }
        }
    }

    fun changeBackgroundColor() {
        if (mainUiModelSubject.value.person.firstName.startsWith("Jirka")) {
            actionChangeBackgroundColor.send(Action.ActionChangeBackgroundColor(Color.RED))
        }
    }

}

sealed class Result {
    class InputFirstNameResult(val value: String) : Result()
    class InputLastNameResult(val value: String) : Result()
    class InputAgeResult(val value: String) : Result()
}

sealed class Action : BaseAction() {
    class ActionChangeBackgroundColor(val color: Int) : Action()
}