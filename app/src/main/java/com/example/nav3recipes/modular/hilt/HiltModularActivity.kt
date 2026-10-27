package com.example.nav3recipes.modular.hilt

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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HiltModularActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        navigator.handleDeepLink(intent)
        setContent {
            HiltModularContent(
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
 * Displays the modular Hilt navigation host using [NavDisplay] and injected entry providers.
 */
@Composable
@Preview
fun HiltModularContent(
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
