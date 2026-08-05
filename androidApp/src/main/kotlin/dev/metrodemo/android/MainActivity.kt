package dev.metrodemo.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.arkivanov.decompose.retainedComponent
import dev.metrodemo.core.common.Platform
import dev.metrodemo.shared.di.createAppGraph
import dev.metrodemo.shared.di.createRootComponent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = retainedComponent { componentContext ->
            createAppGraph(Platform(name = "Android ${Build.VERSION.SDK_INT}"))
                .createRootComponent(componentContext)
        }

        setContent {
            MaterialTheme {
                RootContent(component = root)
            }
        }
    }
}
