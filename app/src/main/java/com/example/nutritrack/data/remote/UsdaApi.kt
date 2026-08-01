package com.example.nutritrack.data.remote

import com.example.nutritrack.data.remote.dto.FoodSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UsdaApi {
    @GET("foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("api_key") apiKey: String = "DEMO_KEY" // Using demo key for testing
    ): FoodSearchResponseDto
}
