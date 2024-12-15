package com.oyetech.composebase.helpers.vibrationHelper

enum class VibrationType(val pattern: LongArray) {
    // DOUBLE(longArrayOf(0, 100, 200, 100)),

    // Hafif bir titreşim, sakinleştirici bir etki bırakmak için
    RAIN2(longArrayOf(0, 50, 200, 50, 200, 100, 300)),

    RAIN(longArrayOf(0, 50, 200, 50, 200, 100, 300)),

    // Hafif ama sürekli bir titreşim, huzur verici bir ritim
    SOFT_PEACE(longArrayOf(0, 100, 150, 100, 250, 100, 400)),

    // Dalgalar halinde gelen titreşim, yumuşak ve sakinleştirici bir his
    WAVES(longArrayOf(0, 150, 250, 150, 300, 150, 500, 300)),

    // Sakin bir başlangıçtan sonra ritmin hızlandığı bir desen, dinamik ve huzurlu bir kombinasyon
    CALM_RHYTHM(longArrayOf(0, 200, 300, 100, 400, 100, 500, 200, 600)),

    // Nazik ve hafif bir dokunuş gibi, sürekli ve yumuşak
    SOFT_TOUCH(longArrayOf(0, 50, 200, 50, 250, 50, 300, 50, 400)),

    // Hafif dalgalarla kesintisiz devam eden bir desen, sakin bir his
    GENTLE_WAVES(longArrayOf(0, 100, 300, 100, 400, 100, 500, 200, 600, 300)),

    // Huzur veren ama kesintili titreşimler, doğadaki sessiz bir yağmur hissi
    QUIET_RAIN(longArrayOf(0, 75, 200, 50, 250, 50, 350, 100, 400, 200)),

    // Daha uzun titreşimlerle derin bir rahatlama hissi
    DEEP_PEACE(longArrayOf(0, 200, 300, 200, 500, 300, 700, 500))
}
