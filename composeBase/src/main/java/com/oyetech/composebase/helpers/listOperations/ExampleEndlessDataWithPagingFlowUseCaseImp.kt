package com.oyetech.composebase.helpers.listOperations

//
// class ExampleEndlessDataWithPagingFlowUseCaseImp  constructor(
//    private val betHistoryGetPinnedCouponsUseCase: BetHistoryGetPinnedCouponsUseCase,
// ) : ExampleEndlessDataWithPagingFlowUseCase {
//
//    private lateinit var currentWidgetGuid: String
//    private lateinit var currentFilter: BetHistoryFilter
//
//    private val pagingHandler = object : PagingOperationHandler<BetHistoryCoupon, BetHistoryFiltered?>() {
//
//        override suspend fun fetchData(pageIndex: Int): BetHistoryFiltered? {
//            return getCoupons(pageIndex, currentWidgetGuid, currentFilter).getOrNull()
//        }
//
//        override fun extractItems(result: BetHistoryFiltered?): List<BetHistoryCoupon> {
//            return result?.coupons ?: emptyList()
//        }
//
//        override fun extractPaging(result: BetHistoryFiltered?): Paging? {
//            return result?.paging
//        }
//    }
//
//    override fun getExampleDataFlow(
//        isInitial: Boolean,
//        widgetGuid: String,
//        filter: BetHistoryFilter,
//    ): Flow<List<BetHistoryCoupon>> {
//        // Store parameters for the handler to use
//        currentWidgetGuid = widgetGuid
//        currentFilter = filter
//
//        // Delegate all pagination logic to the handler
//        return pagingHandler.getDataFlow(isInitial)
//    }
//
//
//
// }
