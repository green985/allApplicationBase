package com.oyetech.composebase.helpers.listOperations

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.Calendar

class ExampleEndlessDataFlowUseCase {

    // for demo purposes only
    var counter = 0

    private val pagingHandler =
        object : ListSizeBasedPagingHandler<ExampleObject>(pageThreshold = 10) {
            override suspend fun fetchData(pageIndex: Int): List<ExampleObject> {
                return dataFlowOperation(pageIndex)
            }
        }

    fun getExampleDataFlow(isInitial: Boolean): Flow<List<ExampleObject>> {
        return pagingHandler.getDataFlow(isInitial)
    }

    private suspend fun dataFlowOperation(pageIndex: Int): ArrayList<ExampleObject> {
        Timber.d("dataFlowOperation called for pageIndex: $pageIndex")
        // Simulate network delay
        delay(2000)

        val listt = arrayListOf<ExampleObject>()
        repeat(20) {
            counter++
            val objectt =
                ExampleObject(
                    Calendar.getInstance().timeInMillis + it,
                    "Item pageSize $pageIndex #$counter"
                )
            listt.add(objectt)
        }

        // control List operation is needed again for control of end of list
        return listt
    }
}
