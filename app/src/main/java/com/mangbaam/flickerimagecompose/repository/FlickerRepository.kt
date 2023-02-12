package com.mangbaam.flickerimagecompose.repository

import com.mangbaam.flickerimagecompose.model.Photo

interface FlickerRepository {
    suspend fun getPhotos(query: String, page: Int, pageSize: Int): Result<List<Photo>>
}
