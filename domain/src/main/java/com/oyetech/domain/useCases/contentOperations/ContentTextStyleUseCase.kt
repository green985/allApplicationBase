package com.oyetech.domain.useCases.contentOperations

import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.models.utils.states.ReadPageOrientation
import com.oyetech.models.utils.states.TextFontEnum
import com.oyetech.models.utils.states.TextFontEnum.DEFAULT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
Created by Erdi Özbek
-13.11.2023-
-16:56-
 **/

class ContentTextStyleUseCase(private var sharedHelperRepository: SharedOperationUseCase) {

    private var fontSizeMultiplierStateFlow = MutableStateFlow(calculateFontSizeMultiplier())

    private var fontStyleStateFlow = MutableStateFlow(getFontStyle())

    private var readPageOrientationStateFlow = MutableStateFlow(getPageOrientation())

    fun calculateFontSizeMultiplier(): Float {
        var scale = sharedHelperRepository.getBibleContentFontSize()
        return scale.toFloat() / 10
    }

    fun getFontSizeMultiplierFlow(): StateFlow<Float> {
        return fontSizeMultiplierStateFlow.asStateFlow()
    }

    fun getFontStyleEnumFlow(): StateFlow<TextFontEnum> {
        return fontStyleStateFlow.asStateFlow()
    }

    fun getReadPageOrientationStateFlow(): StateFlow<ReadPageOrientation> {
        return readPageOrientationStateFlow.asStateFlow()
    }

    fun setFontSizeMultiplier(scale: Int) {
        sharedHelperRepository.setBibleContentFontSize(scale)
        var fontSizeMultiplier = scale.toFloat() / 10
        fontSizeMultiplierStateFlow.value = fontSizeMultiplier
    }

    fun setFontStyle(fontStyleEnum: TextFontEnum) {
        sharedHelperRepository.setBibleContentFont(fontStyleEnum)
        fontStyleStateFlow.value = fontStyleEnum
    }

    fun getFontStyle(): TextFontEnum {
        var fontStyleString = sharedHelperRepository.getBibleContentFont()

        var fontEnum = when (fontStyleString) {
            TextFontEnum.DEFAULT.type -> {
                TextFontEnum.DEFAULT
            }

            TextFontEnum.OPEN_SANS.type -> {
                TextFontEnum.OPEN_SANS
            }

            TextFontEnum.LATO.type -> {
                TextFontEnum.LATO
            }

            TextFontEnum.OSWALD.type -> {
                TextFontEnum.OSWALD
            }

            else -> {
                DEFAULT
            }
        }
        return fontEnum
    }

    fun setPageOrientation(selectedOrientation: ReadPageOrientation) {
        sharedHelperRepository.setPageOrientation(selectedOrientation)
        readPageOrientationStateFlow.value = selectedOrientation
    }

    fun getPageOrientation(): ReadPageOrientation {
        var orientation = sharedHelperRepository.getPageOrientation()

        var orientationEnum = when (orientation) {
            ReadPageOrientation.VERTICAL.type -> ReadPageOrientation.VERTICAL
            ReadPageOrientation.HORIZONTAL.type -> ReadPageOrientation.HORIZONTAL
            else -> {
                ReadPageOrientation.VERTICAL
            }
        }

        return orientationEnum

    }

}