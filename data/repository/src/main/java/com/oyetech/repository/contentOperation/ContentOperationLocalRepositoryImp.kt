package com.oyetech.repository.contentOperation

import com.oyetech.dao.contentDao.ContentLikeDao
import com.oyetech.domain.repository.contentOperation.ContentOperationLocalRepository
import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-11.03.2025-
-20:41-
 **/
class ContentOperationLocalRepositoryImp(
    private val contentLikeDao: ContentLikeDao,
) : ContentOperationLocalRepository {

    override fun getContentLikeList(): List<LikeOperationModel>? {
        return contentLikeDao.getContentLikeList()
    }

    override fun getContentLikeListFlow(contentIdList: List<String>): Flow<List<LikeOperationModel>> {
        return contentLikeDao.getContentLikeListFlow(contentIdList)
    }

    override suspend fun removeLike(contentId: String) {
        contentLikeDao.removeLike(contentId)
    }

    override suspend fun deleteAllList() {
        contentLikeDao.deleteAllList()
    }

    override suspend fun deleteLastList(idList: List<String>) {
        contentLikeDao.deleteLastList(idList)
    }

    override suspend fun getContentSingleLike(contentId: String): LikeOperationModel? {
        return contentLikeDao.getContentSingleLike(contentId)
    }

    override suspend fun addToLikeList(contentLike: LikeOperationModel) {
        contentLikeDao.insert(contentLike)
    }

}
