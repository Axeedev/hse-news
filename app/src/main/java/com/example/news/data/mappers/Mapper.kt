package com.example.news.data.mappers

import com.example.news.data.remote.ArticleDto
import com.example.news.domain.entity.Article
import java.text.SimpleDateFormat
import java.util.Locale


fun ArticleDto.toArticle() = Article(
    title = title ,
    description = description ,
    imageUrl = urlToImage,
    source = source.name ,
    publishedAt = publishedAt.toTimeStamp() ,
    url = url
)


fun String.toTimeStamp(): Long{
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return dateFormatter.parse(this)?.time ?: System.currentTimeMillis()
}