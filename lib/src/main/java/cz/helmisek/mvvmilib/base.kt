package cz.helmisek.mvvmilib

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.helmisek.mvvmilib.monads.Either
import cz.helmisek.mvvmilib.monads.error
import cz.helmisek.mvvmilib.monads.value
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    private val composite = CompositeDisposable()
    private val viewModel by lazy { defineViewModel() }

    abstract fun defineViewModel(): BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.initialize()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        composite.dispose()
        super.onDestroy()
    }

    fun <ER : Throwable, V> Observable<V>.observe(observer: (Either<ER, V>) -> Unit) {
        composite.add(this.subscribe(
                { observer(value(it)) },
                { observer(error(it) as Either<ER, V>) }
        ))
    }

    fun <V> Observable<V>.observeSimple(observer: (V) -> Unit) {
        composite.add(this.subscribe(
                { observer(it) }
        ))
    }

}

abstract class BaseViewModel {

    private val actions = HashMap<String, PublishSubject<BaseAction>>()
    private val models = HashMap<String, BehaviorSubject<BaseModel>>()

    fun initialize() {
        actions.clear()
        models.clear()
        setup()
    }

    abstract fun setup()

    fun <A : BaseModel> findModel(clazz: Class<A>): BehaviorSubject<A> {
        return models[clazz.name] as BehaviorSubject<A>
    }

    fun <A : BaseModel> addModel(clazz: Class<A>, value: A) {
        models.put(clazz.name, BehaviorSubject.createDefault(value))
    }

    fun <A : BaseAction> findAction(clazz: Class<A>): PublishSubject<A> {
        return actions[clazz.name] as PublishSubject<A>
    }

    fun <A : BaseAction> addAction(clazz: Class<A>) {
        actions.put(clazz.name, PublishSubject.create())
    }
}

open class BaseAction
open class BaseModel

inline fun <reified A : Any> BehaviorSubject<A>.update(f: (A) -> A) {
    onNext(f.invoke(value))
}

fun <A> PublishSubject<A>.send(value: A) {
    onNext(value)
}