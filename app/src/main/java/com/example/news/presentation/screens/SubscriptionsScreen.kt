package com.example.news.presentation.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.news.R
import com.example.news.domain.entity.Article
import com.example.news.presentation.ui.theme.CustomIcons
import com.example.news.utils.DateFormatter.toDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    modifier: Modifier = Modifier,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.errors.collect {
            Toast.makeText(
                context,
                it.msg,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBar(
            )
        },
    ) {
        val state by viewModel.state.collectAsState()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = it,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.query,
                    onValueChange = {query ->
                        viewModel.processCommand(SubscriptionsCommand.InputTitle((query)))
                    },
                    label = {
                        Text(
                            text = "What interests you?"
                        )
                    },
                    singleLine = true
                )
                Row(
                    Modifier.padding(vertical = 16.dp)
                ){
                    ButtonFind(isButtonEnabled = state.isFindEnabled) {
                        viewModel.processCommand(
                            SubscriptionsCommand.ClickFind
                        )
                    }
                    if (state.isLoading){
                        Spacer(Modifier.size(16.dp))
                        CircularProgressIndicator()
                    }
                }
            }

            if (state.articles.isNotEmpty()) {
                item {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.DarkGray
                    )
                }

                item {
                    val articlesCount = state.articles.size
                    Text(
                        text = "Found ($articlesCount):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                items(
                    items = state.articles,
                    key = {
                        it.url
                    }
                ) { article ->
                    ArticleCard(
                        article = article
                    )
                }
            } else {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Result is empty"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ButtonFind(
    modifier: Modifier = Modifier,
    isButtonEnabled: Boolean,
    onClick: () -> Unit,
) {

    Button(
        enabled = isButtonEnabled,
        shape = RoundedCornerShape(25),
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.search_24px),
            contentDescription = "add subscription"
        )
        Text(
            text = "Find"
        )
    }
}

@Composable
fun ArticleCard(
    modifier: Modifier = Modifier,
    article: Article,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        article.imageUrl?.let {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                model = it,
                contentDescription = "image of the article",
                contentScale = ContentScale.FillWidth
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = article.title,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        if (article.description.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = article.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = article.source,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = article.publishedAt.toDateFormat(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val context = LocalContext.current
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = CustomIcons.OpenInNew,
                    contentDescription = "open in"
                )
                Spacer(Modifier.size(8.dp))
                Text("Read")
            }
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Look at the article I just found:\n${article.title}\n\n${article.url}")
                    }
                    context.startActivity(intent)
                }
            ) {

                Icon(
                    painter = painterResource(R.drawable.share_24px),
                    contentDescription = "share"
                )
                Spacer(Modifier.size(8.dp))
                Text("Share")
            }


        }

        Spacer(Modifier.size(16.dp))

    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = "News",
                fontWeight = FontWeight.Bold
            )
        },


    )
}

