package dev.metrodemo.core.common.coroutines

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Metro graphs have no `close()`, unlike Koin scopes: nothing is disposed when a screen goes away.
 * Anything with a lifetime must be tied to the Decompose lifecycle by hand, as here.
 */
fun Lifecycle.componentScope(): CoroutineScope {
    val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    doOnDestroy { scope.cancel() }
    return scope
}
