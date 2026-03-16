package com.example.weather_forecast.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.airbnb.lottie.compose.*
import com.example.weather_forecast.data.models.OnboardingPage

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    val context = LocalContext.current

    Column(
        modifier            = Modifier.fillMaxSize().padding(horizontal = 32.dp, ).padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(page.gifRes)
                    .decoderFactory(GifDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier           = Modifier.size(160.dp)
            )
        }

        Spacer(Modifier.height(80.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text  = page.tag,
                style = MaterialTheme.typography.labelSmall.copy(
                    color    = Color.White.copy(alpha = 0.55f),
                    fontSize = 11.sp
                )
            )
        }

        Spacer(Modifier.height(14.dp))


        Text(
            text      = page.title,
            style     = MaterialTheme.typography.labelLarge.copy(
                color      = Color.White,
                fontSize   = 22.sp,
                lineHeight = 30.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))


        Text(
            text      = page.subtitle,
            style     = MaterialTheme.typography.bodyMedium.copy(
                color      = Color.White.copy(alpha = 0.5f),
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}