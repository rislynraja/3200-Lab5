package com.example.k2025_04_22_one_met_image

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.k2025_04_22_one_met_image.models.ArtViewModel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {
    private val artViewModel: ArtViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetImageViewerApp()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MetImageViewerApp() {
        val artList by artViewModel.artList.collectAsState()
        val currentArtObject = artViewModel.getCurrentArtObject()

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("MET Gallery") })
            },
            content = { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    if (artList.isNotEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                currentArtObject?.let { art ->
                                    ImageLoader(
                                        imageUrl = art.primaryImage,
                                        contentDescription = art.title,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } ?: CircularProgressIndicator()
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            currentArtObject?.let { art ->
                                Text(text = art.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = art.artistDisplayName, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { artViewModel.previousArtObject() },
                                    enabled = artList.indexOf(currentArtObject) > 0
                                ) {
                                    Text("Previous")
                                }
                                Button(
                                    onClick = { artViewModel.nextArtObject() },
                                    enabled = artList.indexOf(currentArtObject) < artList.size - 1
                                ) {
                                    Text("Next")
                                }
                            }
                        }
                    } else {
                        CircularProgressIndicator() // to show its loading for the user
                    }
                }
            }
        )
    }

}

@Composable
fun ImageLoader(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    var imageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        imageBitmap = withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val stream = url.openStream()
                val bitmap = BitmapFactory.decodeStream(stream)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}