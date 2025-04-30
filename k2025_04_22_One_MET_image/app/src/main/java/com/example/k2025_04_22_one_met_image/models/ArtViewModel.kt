package com.example.k2025_04_22_one_met_image.models


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.k2025_04_22_one_met_image.models.DataModel
import com.example.k2025_04_22_one_met_image.services.RetrofitClient
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArtViewModel : ViewModel() {

    private val _artList = MutableStateFlow<List<DataModel>>(emptyList())
    val artList: StateFlow<List<DataModel>> = _artList

    private var currentIndex = 0

    init {
        loadArt()
    }

    private fun loadArt() {
        viewModelScope.launch {
            try {
                val objectIDsResponse = try {
                    withContext(Dispatchers.IO) { // use dispatchers to run separately
                        RetrofitClient.apiService.getAllObjectIDs()
                    }
                } catch (e: Exception) {
                    Log.e("ArtViewModel", "Error: ${e.message}", e)
                    return@launch
                }
                val objectIDs = objectIDsResponse.objectIDs.take(10) // getting only first 10 to start
                val artObjects = withContext(Dispatchers.IO) {
                    objectIDs.mapNotNull { id ->
                        try {
                            val artObject = RetrofitClient.apiService.getObject(id)
                            if (artObject.primaryImage.isNotEmpty()) artObject else null
                        } catch (e: Exception) {
                            Log.e("ArtViewModel", "ID $id: ${e.message}", e)
                            null
                        }
                    }
                }

                _artList.value = artObjects
            } catch (e: Exception) {
                Log.e("ArtViewModel", "Error: ${e.message}", e)
            }
        }
    }

    fun getCurrentArtObject(): DataModel? {
        return artList.value.getOrNull(currentIndex)
    }

    fun nextArtObject() { // for button
        if (currentIndex < artList.value.size - 1) {
            currentIndex++
        }
    }

    fun previousArtObject() { // also for button
        if (currentIndex > 0) {
            currentIndex--
        }
    }
}