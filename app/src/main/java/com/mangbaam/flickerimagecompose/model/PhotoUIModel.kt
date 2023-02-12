package com.mangbaam.flickerimagecompose.model

data class PhotoUIModel(
    val id: String = "",
    val owner: String = "",
    val secret: String = "",
    val server: String = "",
    val title: String = "",
) {
    val loadUrl: String
        get() = "https://live.staticflickr.com/$server/${id}_$secret.jpg"
    val loadUrlSmall: String
        get() = "https://live.staticflickr.com/$server/${id}_${secret}_n.jpg"
    val loadUrlOriginal: String
        get() = "https://live.staticflickr.com/$server/${id}_${secret}_o.jpg"
}
