package com.example.moviescomposeapp.data.model

private const val POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342/"

data class Movie(
    val title: String,
    //@Json(name = "poster_path")
    val posterPath: String
) {
    val posterUrl: String by lazy { POSTER_IMAGE_BASE_URL + posterPath }
}
