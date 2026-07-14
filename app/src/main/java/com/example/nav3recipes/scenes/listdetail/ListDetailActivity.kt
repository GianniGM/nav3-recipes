/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nav3recipes.scenes.listdetail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import kotlinx.serialization.Serializable

/**
 * This example shows how to create a list-detail layout using the Scenes API.
 *
 * A `ListDetailScene` will render content in two panes if:
 *
 * - the window width is over 600dp
 * - A `Detail` entry is the last item in the back stack
 * - A `List` entry is in the back stack
 *
 * @see `ListDetailScene`
 */
@Serializable
data object ConversationList : NavKey

@Serializable
data class ConversationDetail(
    val id: Int,
    val colorId: Int
) : NavKey

@Serializable
data object Profile : NavKey

class ListDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

        setContent {
            ListDetailContent()
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview
fun ListDetailContent() {
    Scaffold { paddingValues ->

        val backStack = rememberNavBackStack(ConversationList)
        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

        SharedTransitionLayout {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                sceneStrategies = listOf(listDetailStrategy),
                sharedTransitionScope = this,
                modifier = Modifier.padding(paddingValues),
                predictivePopTransitionSpec = { swipeEdge ->
                    when (swipeEdge) {
                        NavigationEvent.EDGE_LEFT -> {
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        }
                        NavigationEvent.EDGE_RIGHT -> {
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        }
                        else -> { // EDGE_NONE
                            fadeIn() togetherWith scaleOut(targetScale = 0.8f)
                        }
                    }
                },
                entryProvider = entryProvider {
                    entry<ConversationList>(
                        metadata = ListDetailScene.listPane()
                    ) {
                        ConversationListScreen(
                            onConversationClicked = { detailRoute ->
                                backStack.addDetail(detailRoute)
                            }
                        )
                    }
                    entry<ConversationDetail>(
                        metadata = ListDetailScene.detailPane()
                    ) { conversationDetail ->
                        ConversationDetailScreen(
                            conversationDetail = conversationDetail,
                            onBack = { backStack.removeLastOrNull() },
                            onProfileClicked = { backStack.add(Profile) }
                        )
                    }
                    entry<Profile> {
                        ProfileScreen()
                    }
                }
            )
        }
    }
}

private fun NavBackStack<NavKey>.addDetail(detailRoute: ConversationDetail) {

    // Remove any existing detail routes before adding this detail route.
    // In certain scenarios, such as when multiple detail panes can be shown at once, it may
    // be desirable to keep existing detail routes on the back stack.
    removeIf { it is ConversationDetail }
    add(detailRoute)
}