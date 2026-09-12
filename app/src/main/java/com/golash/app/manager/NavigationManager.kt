package com.golash.app.manager

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
    private val _navEvent = Channel<NavIntent>(Channel.BUFFERED)
    val navEvent = _navEvent.receiveAsFlow()

    fun navigateTo(route: String) {
        _navEvent.trySend(NavIntent.NavigateTo(route))
    }

    fun navigateBack() {
        _navEvent.trySend(NavIntent.NavigateBack)
    }

    sealed interface NavIntent {
        data class NavigateTo(val route: String) : NavIntent
        data object NavigateBack : NavIntent
    }
}