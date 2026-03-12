package com.example.weather_forecast.presentation.favourite

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.*
import com.example.weather_forecast.R



private val Surface      = Color(0xFFF8FBFF)

private val Muted        = Color(0xFF7B93B0)
private val ErrorRed     = Color(0xFFEF5350)


@Composable
fun MapPickerScreen(
    pickedLatLng:     LatLng?,
    pickedName:       String,
    cameraState:      CameraPositionState,
    isAddingLocation: Boolean,
    addError:         String?,
    onMapTapped:      (LatLng) -> Unit,
    onPlacePicked:    (LatLng, String) -> Unit,
    onClearPin:       () -> Unit,
    onLocationPicked: (lat: Double, lng: Double, name: String) -> Unit,
    onDismiss:        () -> Unit
) {
    val context = LocalContext.current

    val placesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val place = Autocomplete.getPlaceFromIntent(data)
            place.latLng?.let { latLng ->
                onPlacePicked(latLng, place.name ?: "")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {


        GoogleMap(
            modifier            = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            uiSettings          = MapUiSettings(
                zoomControlsEnabled     = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled       = false
            ),
            onMapClick = { latLng -> onMapTapped(latLng) }
        ) {
            pickedLatLng?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = pickedName.ifEmpty { "Selected Location" }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.38f),
                            Color.Transparent
                        )
                    )
                )
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick  = onDismiss,
                modifier = Modifier
                    .size(46.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector        = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint               =MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Surface(
                onClick = {
                    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                    val intent = Autocomplete
                        .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(context)
                    placesLauncher.launch(intent)
                },
                modifier        = Modifier.weight(1f).height(46.dp),
                shape           = RoundedCornerShape(23.dp),
                color           = Color.White,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier  = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Search,
                        contentDescription = null,
                        tint               = if (pickedName.isEmpty()) Muted else MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(18.dp)
                    )
                    Text(
                        text      = pickedName.ifEmpty { "Search for a place…" },
                        style     = MaterialTheme.typography.bodyMedium.copy(
                            color      = if (pickedName.isEmpty()) Muted else MaterialTheme.colorScheme.secondary,
                            fontWeight = if (pickedName.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                        ),
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MapControlButton(icon = R.drawable.ic_plus, contentDescription = "Zoom in") {

            }
            MapControlButton(icon = R.drawable.ic_minus, contentDescription = "Zoom out") {

            }
            Spacer(Modifier.height(4.dp))

        }


        AnimatedVisibility(
            visible  = pickedLatLng != null,
            enter    = slideInVertically(
                initialOffsetY = { it },
                animationSpec  = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn(),
            exit     = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            pickedLatLng?.let { latLng ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(28.dp),
                        colors    = CardDefaults.cardColors(containerColor = Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier            = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector        = Icons.Rounded.LocationOn,
                                        contentDescription = null,
                                        tint               = MaterialTheme.colorScheme.primary,
                                        modifier           = Modifier.size(26.dp)
                                    )
                                }

                                Column(
                                    modifier            = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text     = pickedName.ifEmpty { "Pinned Location" },
                                        style    = MaterialTheme.typography.titleSmall.copy(
                                            color      = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize   = 15.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.LocationOn,
                                            contentDescription = null,
                                            tint               = Muted,
                                            modifier           = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text  = "${"%.4f".format(latLng.latitude)}°N,  ${"%.4f".format(latLng.longitude)}°E",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color    = Muted,
                                                fontSize = 11.sp,
                                                letterSpacing = 0.3.sp
                                            )
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color     = MaterialTheme.colorScheme.primary
                            )

                            addError?.let { error ->
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier              = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ErrorRed.copy(alpha = 0.08f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Home,
                                        contentDescription = null,
                                        tint               = ErrorRed,
                                        modifier           = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text  = error,
                                        color = ErrorRed,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }


                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                OutlinedButton(
                                    onClick  = onClearPin,
                                    enabled  = !isAddingLocation,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape    = RoundedCornerShape(14.dp),
                                    colors   = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary,
                                    ),
                                    border   = androidx.compose.foundation.BorderStroke(
                                        1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.close),
                                        contentDescription = null,
                                        modifier           = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "Clear Pin",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold , fontSize = 10.sp)
                                    )
                                }

                                Button(
                                    onClick = {
                                        onLocationPicked(latLng.latitude, latLng.longitude, pickedName)
                                    },
                                    enabled  = !isAddingLocation,
                                    modifier = Modifier.weight(1.4f).height(48.dp),
                                    shape    = RoundedCornerShape(14.dp),
                                    colors   = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    if (isAddingLocation) {
                                        CircularProgressIndicator(
                                            modifier    = Modifier.size(18.dp),
                                            color       = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_heart),
                                            contentDescription = null,
                                            modifier           = Modifier.size(14.dp),
                                            tint = Color.White
                                        )
                                        Spacer(Modifier.width(7.dp))
                                        Text(
                                            "Add to Favourites",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MapControlButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick  = onClick,
        modifier = Modifier
            .size(40.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Icon(
            painter        = painterResource(icon),
            contentDescription = contentDescription,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(18.dp)
        )
    }
}