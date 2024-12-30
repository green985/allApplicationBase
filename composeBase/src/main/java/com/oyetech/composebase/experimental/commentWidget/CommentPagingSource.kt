package com.oyetech.composebase.experimental.commentWidget

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.commentScreen.CommentItemUiState
import com.oyetech.composebase.experimental.commentScreen.CommentScreenUiState
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class CommentPagingSource(
    private val commentOperationRepository: FirebaseCommentOperationRepository,
    private val userRepository: FirebaseUserRepository,
    private val commentScreenUiState: MutableStateFlow<CommentScreenUiState>,
) : PagingSource<Int, CommentItemUiState>() {

    lateinit var dispatchers: AppDispatchers

    override fun getRefreshKey(state: PagingState<Int, CommentItemUiState>): Int? {
        val keyy = state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
        Timber.d("getRefreshKey == " + keyy)
        return keyy
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentItemUiState> {

        return try {
            val page = params.key ?: 1
            Timber.d("getRefreshKey load == " + page)

            val list =
                commentOperationRepository.getCommentsWithId(commentScreenUiState.value.contentId)
                    .firstOrNull()?.map {
                        it.createdAt?.let { it1 ->
                            CommentItemUiState(
                                id = it.contentId,
                                commentContent = it.content,
                                createdAt = it1,
                                createdAtString = TimeFunctions.getDateFromLongWithHour(it1.time),
                                username = it.username,
                                isMine = userRepository.isMyContent(it.username)

                            )
                        }
                    }?.filterNotNull() ?: emptyList()

            commentScreenUiState.updateState {
                copy(
                    errorMessage = "",
                    commentList = list.toImmutableList(),
                    isListEmpty = list.isEmpty()
                )
            }

            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = null,
//                prevKey = if (page == 1) null else page - 1,
//                nextKey = if (list.isEmpty()) null else page + 1
            )


        } catch (e: Exception) {
            commentScreenUiState.updateState {
                copy(
                    errorMessage = ErrorHelper.getErrorMessage(e)
                )
            }
            LoadResult.Error(e)
        }

//        return try {
//            withContext(Dispatchers.IO) {
//                val page = params.key ?: 1
//                Timber.d("getRefreshKey load == " + page)
//                val oldList = complexItemViewState.value.items.map { it.quoteId }.toTypedArray()
//                delay(2000)
//                val response =
//                    quoteDataOperationRepository.getQuoteUnseenFlow(oldList).map {
//                        var listt = it
//                        listt = listt.filterNot {
//                            val result = oldList.contains(it.quoteId)
//                            result
//                        }
//                        listt
//                    }.mapToUiState().map {
//                        complexItemViewState.value = complexItemViewState.value.copy(
//                            items = complexItemViewState.value.items.toMutableList().apply {
//                                addAll(it)
//                            }.toPersistentList()
//                        )
//                        it
//                    }.firstOrNull()
//                        ?: emptyList()
//
//
//                LoadResult.Page(
//                    data = response,
//                    prevKey = if (page == 1) null else page - 1,
//                    nextKey = if (response.isEmpty()) null else page + 1
//                )
//            }
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
    }


}