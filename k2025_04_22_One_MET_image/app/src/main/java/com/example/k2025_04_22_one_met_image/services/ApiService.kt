package com.example.k2025_04_22_one_met_image.services

import com.example.k2025_04_22_one_met_image.models.DataModel

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("search?hasImages=true&q=cat")
    suspend fun getAllObjectIDs(): ObjectIDsResponse

    @GET("objects/{objectID}")
    suspend fun getObject(@Path("objectID") objectID: Int): DataModel
}

data class ObjectIDsResponse(
    val total: Int,
    val objectIDs: List<Int>
)