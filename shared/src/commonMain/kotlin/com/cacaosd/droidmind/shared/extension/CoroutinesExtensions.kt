package com.cacaosd.droidmind.shared.extension

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.concurrent.Future
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> Future<T>.await(coroutineContext: CoroutineContext = EmptyCoroutineContext): T =
    withContext(coroutineContext) {
        get()
    }

fun <T> Future<T>.asFlow(): Flow<T> = flow {
    emit(this@asFlow.await(currentCoroutineContext()))
}
