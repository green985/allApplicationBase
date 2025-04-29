package com.oyetech.googlelogin

/**
Created by Erdi Özbek
-5.03.2025-
-20:33-
 **/

class FirebaseGoogleLoginRepositoryImpl() {
// 652520712669-5sudspef6cq60j7drtgr06rm567r0qa2.apps.googleusercontent.com

}

/**
 *
 *     override fun signInWithGoogle() {
 *         googleAuthStateFlow.value = GoogleAuthResponseData(isLoading = true)
 *
 *
 *         activity = getActivityOrSetError("setupGoogleSignIn activity problem") ?: return
 *         setupGoogleSignIn()
 *         setupGoogleSignInLauncher()
 *         val signInIntent = googleSignInClient.signInIntent
 *         googleSignInLauncher.launch(signInIntent)
 *     }
 *
 *     private fun setupGoogleSignIn() {
 *         if (activity == null) {
 *             googleUserStateFlow.value =
 *                 getNewWithException("setupGoogleSignIn activity problem")
 *             return
 *         }
 *
 *         val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
 *             .requestIdToken(QuoteApplicationPrivateKey.QUOTE_GOOGLE_AUTH_CLIENT_ID)
 *             .build()
 *         googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
 *     }
 *
 *     private fun setupGoogleSignInLauncher() {
 *         if (activity == null) {
 *             googleUserStateFlow.value =
 *                 getNewWithException("setupGoogleSignIn activity problem")
 *             return
 *         }
 *         googleSignInLauncher = activityProviderUseCase!!.registerActivityResultLauncher(
 *             activity = activity,
 *             contract = ActivityResultContracts.StartActivityForResult(),
 *             callback = { result ->
 *                 if (result.resultCode == Activity.RESULT_OK) {
 *                     val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
 *                     handleSignInResult(task.result)
 *                 } else {
 *                     googleUserStateFlow.value =
 *                         getNewWithException("setupGoogleSignInLauncher Google sign in failed")
 *                 }
 *             }
 *         )
 *     }
 *
 *     private fun handleSignInResult(account: GoogleSignInAccount) {
 *         val credential = GoogleAuthProvider.getCredential(account.idToken, null)
 *         activity?.let {
 *             firebaseAuth.signInWithCredential(credential)
 *                 .addOnCompleteListener(it) { task ->
 *                     if (task.isSuccessful) {
 *                         // Sign-in success
 *                         val user = firebaseAuth.currentUser
 *                         // Do something with the user object
 *                         googleAuthStateFlow.value =
 *                             GoogleAuthResponseData(
 *                                 id = user?.uid,
 *                                 email = user?.email,
 *                                 name = user?.displayName,
 *                                 timeStamp = System.currentTimeMillis()
 *                             )
 *                     } else {
 *                         googleUserStateFlow.value =
 *                             getNewWithException("handleSignInResult Google sign in failed")
 *                     }
 *                 }
 *         }
 *     }
 *
 *     private fun getActivityOrSetError(errorString: String): ComponentActivity? {
 *         val activity = activityProviderUseCase.getCurrentActivity()
 *
 *         if (activity == null) {
 *             googleUserStateFlow.value = getNewWithException(errorString)
 *             return null
 *         }
 *         return activity as? ComponentActivity
 *     }
 *
 */