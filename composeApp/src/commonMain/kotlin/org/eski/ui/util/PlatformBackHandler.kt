package org.eski.ui.util

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformBackHandler(onBack: () -> Unit)