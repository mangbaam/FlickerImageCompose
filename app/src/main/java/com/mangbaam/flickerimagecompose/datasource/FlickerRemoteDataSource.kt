package com.mangbaam.flickerimagecompose.datasource

import com.mangbaam.flickerimagecompose.BuildConfig
import com.mangbaam.flickerimagecompose.model.FlickerException
import com.mangbaam.flickerimagecompose.model.Photo
import com.mangbaam.flickerimagecompose.network.FlickerService

class FlickerRemoteDataSource(private val service: FlickerService) : FlickerDataSource {
    override suspend fun getPhotos(query: String, page: Int, pageSize: Int): Result<List<Photo>> {
        return service.getPhotos(
            apiKey = BuildConfig.FLICKER_API_KEY,
            text = query,
            page = 1,
            pageSize = 20,
        ).runCatching {
            if (code() == 200) {
                body()?.photos?.photo ?: emptyList()
            } else {
                throw (FlickerException.fromCode(code()))
            }
        }
    }
}
