package cz.helmisek.mvvmilib.monads

sealed class Either<out E, out V> {
    data class Error<out E>(val error: E) : Either<E, Nothing>()
    data class Value<out V>(val value: V) : Either<Nothing, V>()
}

// creators
fun <V> value(value: V): Either<Nothing, V> = Either.Value(value)

fun <E> error(value: E): Either<E, Nothing> = Either.Error(value)

fun <V> either(action: () -> V): Either<Exception, V> = try {
    value(action())
} catch (e: Exception) {
    error(e)
}

// composing

// functor
inline infix fun <E, V, V2> Either<E, V>.map(func: (V) -> V2): Either<E, V2> = when (this) {
    is Either.Error -> this
    is Either.Value -> Either.Value(func(value))
}

// applicative
fun <F, A, B> Either<F, (A) -> B>.apply(f: Either<F, A>): Either<F, B> = when (this) {
    is Either.Value -> f.map(this.value)
    is Either.Error -> this
}

// chainable - basically chain operator
inline infix fun <E, V, V2> Either<E, V>.flatMap(func: (V) -> Either<E, V2>): Either<E, V2> = when (this) {
    is Either.Error -> this
    is Either.Value -> func(value)
}

inline infix fun <E, E2, V> Either<E, V>.mapError(func: (E) -> E2): Either<E2, V> = when (this) {
    is Either.Error -> Either.Error(func(error))
    is Either.Value -> this
}

// recover from error
inline infix fun <E, V> Either<E, V>.onErrorNext(func: (E) -> Either<E, V>): Either<E, V> = when (this) {
    is Either.Error -> func(error)
    is Either.Value -> this
}

inline fun <E, V, A> Either<E, V>.fold(e: (E) -> A, v: (V) -> A): A = when (this) {
    is Either.Error -> e(this.error)
    is Either.Value -> v(this.value)
}

fun <E, V> Either<E, V>.flip(): Either<V, E> = fold({ value(it) }, { error(it) })