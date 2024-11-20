import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> List<T>.takeRandom(n: Int): List<T> =
    shuffled().take(n)

fun <T> List<T>.replaceAt(index: Int, item: T): List<T> =
    toMutableList().apply { this[index] = item }

fun <T> List<T>.replaceIf(predicate: (T) -> Boolean, item: T): List<T> =
    map { if (predicate(it)) item else it }

fun <T> List<T>.updateAt(index: Int, update: (T) -> T): List<T> =
    toMutableList().apply { this[index] = update(this[index]) }

fun <T> List<T>.updateIf(predicate: (T) -> Boolean, update: (T) -> T): List<T> =
    map { if (predicate(it)) update(it) else it }

fun <T> List<T>.moveItem(fromIndex: Int, toIndex: Int): List<T> =
    toMutableList().apply {
        add(toIndex, removeAt(fromIndex))
    }

fun <T> List<T>.insertAt(index: Int, item: T): List<T> =
    toMutableList().apply {
        add(index, item)
    }

fun <T> List<T>.removeIf(predicate: (T) -> Boolean): List<T> =
    filterNot(predicate)

fun <T> Flow<List<T>>.mapItems(transform: suspend (T) -> T): Flow<List<T>> =
    map { list -> list.map { transform(it) } }

fun <T> List<T>.forEachIndexedReversed(action: (Int, T) -> Unit) {
    for (i in lastIndex downTo 0) {
        action(i, get(i))
    }
} 