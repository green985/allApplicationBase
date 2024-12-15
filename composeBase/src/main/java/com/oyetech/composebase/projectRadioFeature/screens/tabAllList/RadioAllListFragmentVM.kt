package com.oyetech.composebase.projectRadioFeature.screens.tabAllList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems.DeleteList
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems.Sort
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems.Timer
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent.BackButtonClick
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent.OnActionButtonClick
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.core.appUtil.AppUtil
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.useCases.TimerOperationUseCase
import com.oyetech.models.radioProject.enums.RadioListEnums
import com.oyetech.models.radioProject.enums.RadioListEnums.Country
import com.oyetech.models.radioProject.enums.RadioListEnums.Favorites
import com.oyetech.models.radioProject.enums.RadioListEnums.History
import com.oyetech.models.radioProject.enums.RadioListEnums.Idle
import com.oyetech.models.radioProject.enums.RadioListEnums.Languages
import com.oyetech.models.radioProject.enums.RadioListEnums.Last_Change
import com.oyetech.models.radioProject.enums.RadioListEnums.Last_Click
import com.oyetech.models.radioProject.enums.RadioListEnums.Local
import com.oyetech.models.radioProject.enums.RadioListEnums.Search
import com.oyetech.models.radioProject.enums.RadioListEnums.Tag
import com.oyetech.models.radioProject.enums.RadioListEnums.Top_Click
import com.oyetech.models.radioProject.enums.RadioListEnums.Top_Voted
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-30.11.2022-
-16:37-
 **/

class RadioAllListFragmentVM(
    appDispatchers: AppDispatchers,
    private val radioStationListOperationUseCase: RadioStationListOperationUseCase,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(RadioAllListUiState())

    val radioToolbarState = MutableStateFlow(
        RadioToolbarState(
            title = "Radio Everyone",
            showBackButton = false,
            actionButtonState = persistentListOf(
                RadioToolbarActionItems.Search(R.drawable.ic_search),
                RadioToolbarActionItems.Timer(R.drawable.ic_timer),
                RadioToolbarActionItems.Sort(R.drawable.ic_sort),
            )
        )
    )

    val timerOperationUseCase: TimerOperationUseCase by KoinJavaComponent.inject(
        TimerOperationUseCase::class.java
    )

    var timerLeftMinLiveData = MutableLiveData<Int>(0)

    init {
//        sharedPrefUseCase.increaseAppOpenCount()
//        controlReviewCanShow()
        observeTimerOperationLeftSeconds()
        prepareUiState()
    }

    private fun prepareUiState() {
        val tabEnumList = prepareRadioPathList()
        val tabNameList = tabEnumList.map {
            getTabLayoutTextWithListType(it)
        }.toImmutableList()

        uiState.value = RadioAllListUiState(
            tabNameList = tabNameList, selectedIndex = 0, tabEnumList = tabEnumList

        )
    }

    private fun prepareRadioPathList(): ImmutableList<RadioListEnums> {
        val countryCode = AppUtil.getCountryCode(context)

        val list = arrayListOf<RadioListEnums>()

        if (countryCode.isNotBlank()) {
            list.add(Local)
        }
        list.add(Top_Click)
        list.add(Top_Voted)
        list.add(Languages)
        list.add(Last_Change)
        list.add(Last_Click)
        list.add(Country)

        return list.toImmutableList()
    }

    private fun observeTimerOperationLeftSeconds() {
        viewModelScope.launch(getDispatcherIo()) {
            timerOperationUseCase.getTimerCountFlow().onEach {
                val minInt = (it / 60).toInt()
                val value = radioToolbarState.value.timeLeftBadge.toInt()
                if (value != minInt) {
                    radioToolbarState.updateState {
                        copy(
                            timeLeftBadge = minInt
                        )
                    }
                }
            }.collect()
        }
    }

    fun getTabLayoutTextWithListType(listType: RadioListEnums): String {
        var tabString = ""
        Timber.d("listyeeee == " + listType.name)

        fun getTextWithListType(stringRes: Int): String {
            tabString = context.getString(stringRes)
            return tabString
        }



        when (listType) {
            Local -> {
                getTextWithListType(R.string.action_local)
            }

            History -> {}
            Favorites -> {}
            Last_Click -> {
                getTextWithListType(R.string.action_currently_playing)
            }

            Last_Change -> {
                getTextWithListType(R.string.action_changed_lately)
            }

            Top_Voted -> {
                getTextWithListType(R.string.action_top_vote)
            }

            Top_Click -> {
                getTextWithListType(R.string.action_top_click)
            }

            Languages -> {
                getTextWithListType(R.string.action_languages)
            }

            Country -> {
                getTextWithListType(R.string.action_countries)
            }

            Tag -> {}
            Search -> {}
            Idle -> {}
        }

        return tabString
    }

    fun handleToolbarAction(event: RadioToolbarEvent, listType: RadioListEnums) {
        when (event) {
            is OnActionButtonClick -> {
                when (event.actionItem) {
                    else -> {
                        when (event.actionItem) {
                            is RadioToolbarActionItems.Search -> {
                                Timber.d("Search")
                            }

                            is Timer -> {
                                Timber.d("Timer")
                            }

                            is Sort -> {
                                Timber.d("Sort")
                                sortList(listType)
                            }

                            is DeleteList -> {
                                Timber.d("DeleteList")
                            }
                        }
                    }
                }
            }

            is BackButtonClick -> {
                Timber.d("BackButtonClick")
            }
        }

    }

    private fun sortList(listType: RadioListEnums) {
        viewModelScope.launch {
            radioStationListOperationUseCase.triggerSortOperationClickNavigator(listType.name)
        }
    }

}