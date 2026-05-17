package com.example.news.data.repository

import com.example.news.data.mappers.toArticle
import com.example.news.data.remote.ApiService
import com.example.news.domain.Resource
import com.example.news.domain.entity.Article
import com.example.news.domain.repository.NewsRepository
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: ApiService
)  : NewsRepository {

    override suspend fun getHeadlines(): Resource<List<Article>> {

        return try {

            val response = api.getHeadlines()

            if (response.isSuccessful) {

                Resource.Success(
                    response.body()?.articles?.map { it.toArticle() } ?: emptyList()
                )

            } else {

                Resource.Error(
                    message = "HTTP ${response.code()}"
                )
            }

        } catch (e: SocketTimeoutException) {

            Resource.Error(
                message = "Timeout error",
                throwable = e
            )

        } catch (e: IOException) {

            Resource.Error(
                message = "No internet connection",
                throwable = e
            )

        } catch (e: Exception) {

            Resource.Error(
                message = "Unknown error",
                throwable = e
            )
        }
    }

    override suspend fun getArticles(
        query: String
    ): Resource<List<Article>> {

        return try {

            val response = api.getArticles(query)

            if (response.isSuccessful) {

                Resource.Success(
                    response.body()?.articles?.map { it.toArticle() } ?: emptyList()
                )

            } else {

                Resource.Error(
                    message = "HTTP ${response.code()}"
                )
            }

        } catch (e: Exception) {

            Resource.Error(
                message = e.message ?: "Unknown error",
                throwable = e
            )
        }
    }
}