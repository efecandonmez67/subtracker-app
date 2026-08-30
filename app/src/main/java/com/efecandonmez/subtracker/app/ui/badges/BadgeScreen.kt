package com.efecandonmez.subtracker.app.ui.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.efecandonmez.subtracker.app.ui.theme.GradientEndLight
import com.efecandonmez.subtracker.app.ui.theme.GradientStartLight

@Composable
fun BadgeScreen(viewModel: BadgeViewModel) {
    val earnedBadges by viewModel.earnedBadges.collectAsState()
    val earnedTypes = earnedBadges.map { it.badgeType }.toSet()

    LaunchedEffect(Unit) { viewModel.loadBadges() }

    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Brush.horizontalGradient(listOf(GradientStartLight, GradientEndLight))),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "Rozetlerim",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(24.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ALL_BADGE_TYPES) { type ->
                val info = BADGE_INFO_MAP[type]!!
                val earned = earnedTypes.contains(type)

                Box(
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (earned) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            info.emoji,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.alpha(if (earned) 1f else 0.3f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            info.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (earned) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            info.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}