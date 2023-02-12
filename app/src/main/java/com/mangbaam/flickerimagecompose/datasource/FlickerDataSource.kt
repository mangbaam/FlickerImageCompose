package com.mangbaam.flickerimagecompose.datasource

import com.mangbaam.flickerimagecompose.model.Photo

interface FlickerDataSource {
    suspend fun getPhotos(query: String, page: Int, pageSize: Int): Result<List<Photo>>
}
