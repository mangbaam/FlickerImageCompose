package com.mangbaam.flickerimagecompose.repository

import com.mangbaam.flickerimagecompose.datasource.FlickerDataSource
import com.mangbaam.flickerimagecompose.model.Photo

class FlickerRepositoryImpl(private val dataSource: FlickerDataSource) : FlickerRepository {
    override suspend fun getPhotos(query: String, page: Int, pageSize: Int): Result<List<Photo>> {
        return dataSource.getPhotos(query, page, pageSize)
    }
}
