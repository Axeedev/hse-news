@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.news.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.domain.Resource
import com.example.news.domain.entity.Article
import com.example.news.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel(){

    private val _state : MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState())
    val state
        get() = _state.asStateFlow()

    val errors = MutableSharedFlow<Error>()

    init {
        viewModelScope.launch {

            val result: Resource<List<Article>> = repository.getHeadlines()
            when(result){
                is Resource.Error<*> -> {
                    errors.emit(Error(result.message))

                }
                is Resource.Success<List<Article>> -> {

                    _state.update { it.copy(articles = result.data) }
                }
            }

        }
    }

    fun processCommand(command: SubscriptionsCommand){
        when(command){
            SubscriptionsCommand.ClickFind -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    val topic = _state.value.query
                    val result = repository.getArticles(topic)
                    when(result){
                        is Resource.Error<*> -> {

                            errors.emit(Error(result.message))

                            _state.update { it.copy(articles = listOf()) }

                        }
                        is Resource.Success<List<Article>> -> {

                            _state.update { it.copy(articles = result.data) }
                        }
                    }

                    _state.update { it.copy(isLoading = false) }
                }
            }
            is SubscriptionsCommand.InputTitle -> {
                _state.update {

                    it.copy(query = command.query)
                }
            }
        }
    }
}

sealed interface SubscriptionsCommand{

    data class InputTitle(val query: String) : SubscriptionsCommand

    data object ClickFind : SubscriptionsCommand

}

data class ScreenState(
    val query: String = "",
    val articles: List<Article> = listOf(),
    val isLoading: Boolean = false
){
    val isFindEnabled: Boolean
        get() = query.isNotBlank()

}

data class Error(val msg: String)