package com.oyetech.composebase.projectQuotesFeature.authorListScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.composebase.projectQuotesFeature.views.ItemAuthorView
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-11.01.2025-
-15:51-
 **/

@Composable
fun AuthorListScreen(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit,
) {

    val vm = koinViewModel<AuthorListVM>()
    val lazyPagingItems = vm.authorPage.collectAsLazyPagingItems()

    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            BasePagingListScreen(
                items = lazyPagingItems, // This parameter is abstracted, not used here
                itemKey = { author -> author.authorId },
                onBindItem = { quote ->
                    ItemAuthorView(
                        authorDisplayName = quote.authorName,
                        authorImage = quote.authorImage,
                        onClick = {
                            vm.onEvent(QuoteAuthorEvent.AuthorClicked(quote.authorId))
                        }
                    )

                },
            )
        }
    }
}