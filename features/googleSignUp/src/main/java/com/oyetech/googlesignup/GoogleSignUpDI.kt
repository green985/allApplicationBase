package com.oyetech.googlesignup

import com.oyetech.googlesignup.signUp.GoogleSignUpVM
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-23.06.2024-
-17:11-
 **/

object GoogleSignUpDI {
    val googleSignUpModule = module {
        viewModelOf(::GoogleSignUpVM)
    }
}