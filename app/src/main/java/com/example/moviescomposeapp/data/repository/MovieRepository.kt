package com.example.moviescomposeapp.data.repository

import com.example.moviescomposeapp.data.model.Movie
import com.example.moviescomposeapp.data.model.MovieGenre
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getTopRatedMoviesStream(): Flow<List<Movie>>
    fun getMoviesStream(genre: MovieGenre): Flow<List<Movie>>
    suspend fun refreshTopRated()
    suspend fun refreshGenre(genre: MovieGenre)
}
