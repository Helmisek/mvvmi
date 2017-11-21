package cz.helmisek.mvvmi.main

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import cz.helmisek.mvvmilib.BaseActivity
import cz.helmisek.mvvmilib.BaseViewModel
import cz.helmisek.mvvmi.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val vm = MainViewModel()

    override fun defineViewModel(): BaseViewModel = vm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerInputs()

        runDisposable(vm.mainUiModelSubject.subscribe {
            vm.changeBackgroundColor()
            output_age.text = it.person.age
            output_first_name.text = it.person.firstName
            output_last_name.text = it.person.lastName
        })

        runDisposable(vm.actionChangeBackgroundColor.subscribe {
            Log.v("Action received", "${it.color}")
        })

    }

    private fun registerInputs() {
        registerInput(input_first_name, { Result.InputFirstNameResult(it) })
        registerInput(input_last_name, { Result.InputLastNameResult(it) })
        registerInput(input_age, { Result.InputAgeResult(it) })
    }

    private fun <A : Result> registerInput(textView: TextView, transform: (value: String) -> A) {
        runDisposable(RxTextView.afterTextChangeEvents(textView).flatMap {
            Observable.just(it.editable()?.toString())
        }.subscribe({
            vm.notifyUiModelResult(transform.invoke(it ?: ""))
        }))
    }

}