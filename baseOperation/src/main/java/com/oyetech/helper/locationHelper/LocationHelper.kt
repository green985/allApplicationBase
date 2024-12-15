package com.oyetech.helper.locationHelper

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver

/**
Created by Erdi Özbek
-22.05.2022-
-14:46-
 **/

class LocationHelper(private var applicationContext: Context) : DefaultLifecycleObserver {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    }
    /*

    private var repository: LocationHelperRepository? = null

    var locationUpdateLiveData = MutableLiveData<LocationRequestBody>()

    init {
        Timber.d("init locationHelper")
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // The Fused Location Provider provides access to location APIs.
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)

    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    var cancellationTokenSource = CancellationTokenSource()

    fun setRepository(repository: LocationHelperRepository?) {
        this.repository = repository
    }

    fun requestPermissionAgain() {
        repository?.requestPermissionAgain()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Timber.d("onStart called")
        cancellationTokenSource = CancellationTokenSource()
        // requestLocation()
    }

    fun cancelCancellationTokenSource() {
        doInTryCatch {
            cancellationTokenSource.cancel()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        cancelCancellationTokenSource()
        super.onStop(owner)
        Timber.d("onStop called")
    }

    fun requestLocation() {
        // requestCurrentLocation()

        // FIXME: Will be fixed after location enable.
        locationUpdateLiveData.value =
            LocationRequestBody(0.0, 0.0)
        return

        var permissionResult = applicationContext.checkPermissionForLocation()
        if (permissionResult) {
            requestCurrentLocation()
        } else {
            requestPermissionAgain()
        }
    }

    /**
     * Gets current location.
     * Note: The code checks for permission before calling this method, that is, it's never called
     * from a method with a missing permission. Also, I include a second check with my extension
     * function in case devs just copy/paste this code.
     */
    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {

        Timber.d("requestCurrentLocation()")
        if (applicationContext.checkPermissionForLocation()) {

            // Returns a single current location fix on the device. Unlike getLastLocation() that
            // returns a cached location, this method could cause active location computation on the
            // device. A single fresh location will be returned if the device location can be
            // determined within reasonable time (tens of seconds), otherwise null will be returned.
            //
            // Both arguments are required.
            // PRIORITY type is self-explanatory. (Other options are PRIORITY_BALANCED_POWER_ACCURACY,
            // PRIORITY_LOW_POWER, and PRIORITY_NO_POWER.)
            // The second parameter, [CancellationToken] allows the activity to cancel the request
            // before completion.
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )

            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                if (task.isSuccessful && task.result != null) {
                    val result: Location = task.result
                    "Location (success): ${result.latitude}, ${result.longitude}"
                    // repository?.locationResultSuccess(result.latitude, result.longitude)

                    locationUpdateLiveData.value =
                        LocationRequestBody(result.latitude, result.longitude)
                } else {
                    val exception = task.exception
                    Log.d(TAG, "Location (failure): $exception")
                    repository?.locationResultError(exception)
                }
            }
        } else {
            Timber.d("permission not found.")
            repository?.requestPermissionAgain()
        }
    }

    interface LocationHelperRepository {
        fun locationResultError(exception: Exception?)

        fun requestPermissionAgain()
    }

     */
}
