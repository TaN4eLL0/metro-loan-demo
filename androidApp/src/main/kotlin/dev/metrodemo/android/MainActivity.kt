package dev.metrodemo.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import dev.metrodemo.core.common.Platform
import dev.metrodemo.shared.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val graph = createGraphFactory<AppGraph.Factory>()
            .create(Platform(name = "Android ${Build.VERSION.SDK_INT}"))

        setContent {
            MaterialTheme {
                GraphSmokeScreen(graph = graph)
            }
        }
    }
}
