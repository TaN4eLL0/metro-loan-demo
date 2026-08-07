package dev.metrodemo.shared.di

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import dev.metrodemo.core.common.Platform
import dev.metrodemo.shared.RootComponent
import platform.UIKit.UIDevice

/**
 * The single call Swift makes. Everything Metro-related stays on the Kotlin side: Swift cannot ask
 * the graph for a type, it can only use what is declared here.
 */
fun createIosRootComponent(): RootComponent {
    val device = UIDevice.currentDevice
    return createAppGraph(Platform(name = "${device.systemName} ${device.systemVersion}"))
        .createRootComponent(DefaultComponentContext(lifecycle = ApplicationLifecycle()))
}
