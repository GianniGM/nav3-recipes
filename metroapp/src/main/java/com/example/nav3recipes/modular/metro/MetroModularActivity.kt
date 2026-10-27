package com.example.nav3recipes.modular.metro

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey

@ContributesIntoMap(ActivityScope::class, binding<Activity>())
@ActivityKey
@Inject
class MetroModularActivity(
    private val navigator: Navigator,
    private val entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>
) : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        navigator.handleDeepLink(intent)
        setContent {
            MetroModularContent(
                backStack = navigator.backStack,
                onBack = { navigator.goBack() },
                entryProvider = entryProvider {
                    entryProviderScopes.forEach { builder -> this.builder() }
                }
            )
        }
    }
}

/**
 * Displays the modular Metro navigation host using [NavDisplay] and injected entry providers.
 */
@Composable
@Preview
fun MetroModularContent(
    backStack: List<NavKey> = remember { mutableStateListOf(ConversationList) },
    onBack: () -> Unit = {},
    entryProvider: (NavKey) -> NavEntry<NavKey> = { key -> NavEntry(key) { Text("Empty") } }
) {
    Scaffold { paddingValues ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(paddingValues),
            onBack = onBack,
            entryProvider = entryProvider
        )
    }
}
