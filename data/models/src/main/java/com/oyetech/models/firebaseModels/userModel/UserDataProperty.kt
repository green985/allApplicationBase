package com.oyetech.models.firebaseModels.userModel

// Auth katmaninda UserDataProperty ismi kullanilirken mevcut profile modeli yeniden kullanilir.
typealias UserDataProperty = UserProfileProperty

fun UserDataProperty.isProfileCompletedForAuth(): Boolean {
	return username.isNotBlank() &&
			age.isNotBlank() &&
			gender.isNotBlank()
}

