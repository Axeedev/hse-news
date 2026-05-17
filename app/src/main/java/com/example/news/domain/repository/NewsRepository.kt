package com.example.news.domain.repository

import com.example.news.domain.Resource
import com.example.news.domain.entity.Article

interface NewsRepository {

    suspend fun getHeadlines(): Resource<List<Article>>

    suspend fun getArticles(
        query: String
    ): Resource<List<Article>>
}