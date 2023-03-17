package com.example.moviescomposeapp.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviescomposeapp.WhileUiSubscribed
import com.example.moviescomposeapp.core.Result
import com.example.moviescomposeapp.core.asResult
import com.example.moviescomposeapp.data.model.Movie
import com.example.moviescomposeapp.data.model.MovieGenre.ACTION
import com.example.moviescomposeapp.data.model.MovieGenre.ANIMATION
import com.example.moviescomposeapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val topRatedMovies: TopRatedMoviesUiState,
    val actionMovies: ActionMoviesState,
    val animationMovies: AnimationMoviesUiState,
    val isRefreshing: Boolean,
    val isError: Boolean
)

@Immutable
sealed interface TopRatedMoviesUiState {
    data class Success(val movies: List<Movie>) : TopRatedMoviesUiState
    object Error : TopRatedMoviesUiState
    object Loading : TopRatedMoviesUiState
}

@Immutable
sealed interface ActionMoviesState {
    data class Success(val movies: List<Movie>) : ActionMoviesState
    object Error : ActionMoviesState
    object Loading : ActionMoviesState
}

@Immutable
sealed interface AnimationMoviesUiState {
    data class Success(val movies: List<Movie>) : AnimationMoviesUiState
    object Error : AnimationMoviesUiState
    object Loading : AnimationMoviesUiState
}

class HomeViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val topRatedMovies: Flow<Result<List<Movie>>> =
        movieRepository.getTopRatedMoviesStream().asResult()

    private val actionMovies: Flow<Result<List<Movie>>> =
        movieRepository.getMoviesStream(ACTION).asResult()

    private val animationMovies: Flow<Result<List<Movie>>> =
        movieRepository.getMoviesStream(ANIMATION).asResult()

    private val isRefreshing = MutableStateFlow(false)

    private val isError = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        topRatedMovies,
        actionMovies,
        animationMovies,
        isRefreshing,
        isError
    ) { topRatedResult, actionMoviesResult, animationMoviesResult, refreshing, errorOcurred ->

        val topRated: TopRatedMoviesUiState = when (topRatedResult) {
            is Result.Success -> TopRatedMoviesUiState.Success(topRatedResult.data)
            is Result.Loading -> TopRatedMoviesUiState.Loading
            is Result.Error -> TopRatedMoviesUiState.Error
        }

        val action: ActionMoviesState = when (actionMoviesResult) {
            is Result.Success -> ActionMoviesState.Success(actionMoviesResult.data)
            is Result.Loading -> ActionMoviesState.Loading
            is Result.Error -> ActionMoviesState.Error
        }

        val animation: AnimationMoviesUiState = when (animationMoviesResult) {
            is Result.Success -> AnimationMoviesUiState.Success(animationMoviesResult.data)
            is Result.Loading -> AnimationMoviesUiState.Loading
            is Result.Error -> AnimationMoviesUiState.Error
        }

        HomeUiState(
            topRated,
            action,
            animation,
            refreshing,
            errorOcurred
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = HomeUiState(
                TopRatedMoviesUiState.Loading,
                ActionMoviesState.Loading,
                AnimationMoviesUiState.Loading,
                isRefreshing = false,
                isError = false
            )
        )
}
