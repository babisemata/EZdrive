package com.example.ezdrive.maps

import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchMapScreen(
    address: String,
    branchName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Init osmdroid config
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        )
    }

    // Geocode alamat
    var coord by remember { mutableStateOf<LatLng?>(null) }
    LaunchedEffect(address) {
        runCatching {
            Geocoder(context).getFromLocationName(address, 1)
        }.onSuccess { list ->
            if (!list.isNullOrEmpty()) {
                val loc = list[0]
                coord = LatLng(loc.latitude, loc.longitude)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(branchName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (coord == null) {
                // Loading
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                // Embed osmdroid MapView
                AndroidView(
                    factory = {
                        MapView(it).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            controller.setZoom(15.0)
                            controller.setCenter(org.osmdroid.util.GeoPoint(coord!!.latitude, coord!!.longitude))

                            // Tambahkan marker
                            val marker = Marker(this)
                            marker.position = org.osmdroid.util.GeoPoint(coord!!.latitude, coord!!.longitude)
                            marker.title = branchName
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            overlays.add(marker)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
