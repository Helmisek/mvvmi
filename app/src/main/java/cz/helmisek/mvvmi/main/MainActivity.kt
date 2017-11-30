package cz.helmisek.mvvmi.main

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import cz.helmisek.mvvmi.R
import cz.helmisek.mvvmilib.BaseActivity
import cz.helmisek.mvvmilib.BaseViewModel
import cz.helmisek.mvvmilib.monads.Either
import cz.helmisek.mvvmilib.monads.error
import cz.helmisek.mvvmilib.monads.fold
import cz.helmisek.mvvmilib.monads.value
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val vm = MainViewModel()

    override fun defineViewModel(): BaseViewModel = vm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerInputs()

        vm.mainUiModelSubject.observeSimple {
            vm.changeBackgroundColor()
            output_age.text = it.person.age
            output_first_name.text = it.person.firstName
            output_last_name.text = it.person.lastName
        }

        vm.actionChangeBackgroundColor.observeSimple {
            Log.v("Action received", "${it.color}")
        }

    }

    private fun registerInputs() {
        registerInput(input_first_name, {
            if (it.length <= 5) {
                value(Result.InputFirstNameResult(it))
            } else {
                error(InputValidationError("First name has got to be shorter than 5"))
            }
        })
        registerInput(input_last_name, {
            if (it.length >= 5) {
                value(Result.InputLastNameResult(it))
            } else {
                error(InputValidationError("Last name has got to be longer than 5"))
            }
        })
        registerInput(input_age, {
            if (it != "" && it.toInt() <= 100) {
                value(Result.InputAgeResult(it))
            } else {
                error(InputValidationError("Age name has got to be less than or 100"))
            }
        })
    }

    private fun <A : Result> registerInput(textView: TextView, validation: (String) -> Either<InputValidationError, A>) {
        RxTextView.afterTextChangeEvents(textView).flatMap {
            Observable.just(validation(it.editable()?.toString() ?: ""))
        }.observeSimple {
            it.fold({
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }, {
                vm.notifyUiModelResult(it)
            })
        }
    }
}

class InputValidationError(message: String) : Throwable(message)