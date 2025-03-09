package org.eski.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


fun <T> StateFlow<T>.launchCollect(scope: CoroutineScope, lambda: ((T) -> Unit)) {
  scope.launch {
    collect { flowState ->
      lambda.invoke(flowState)
    }
  }
}

fun <T> StateFlow<T>.launchCollectLatest(scope: CoroutineScope, lambda: ((T) -> Unit)) {
  scope.launch {
    collectLatest { flowState ->
      lambda.invoke(flowState)
    }
  }
}