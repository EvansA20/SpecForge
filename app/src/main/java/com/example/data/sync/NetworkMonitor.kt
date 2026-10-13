package com.example.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isDeviceOnline = MutableStateFlow(checkInitialConnectivity())
    private val _isSimulatedOffline = MutableStateFlow(false)

    private val _isOnline = MutableStateFlow(checkInitialConnectivity())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    val isSimulatedOffline: StateFlow<Boolean> = _isSimulatedOffline.asStateFlow()

    init {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager?.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isDeviceOnline.value = true
                    updateEffectiveOnlineState()
                }

                override fun onLost(network: Network) {
                    _isDeviceOnline.value = false
                    updateEffectiveOnlineState()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            // Default to online if callback registration is restricted
            _isDeviceOnline.value = true
            updateEffectiveOnlineState()
        }
    }

    private fun checkInitialConnectivity(): Boolean {
        return try {
            val network = connectivityManager?.activeNetwork ?: return true
            val caps = connectivityManager.getNetworkCapabilities(network) ?: return true
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            true
        }
    }

    fun setSimulateOffline(simulateOffline: Boolean) {
        _isSimulatedOffline.value = simulateOffline
        updateEffectiveOnlineState()
    }

    private fun updateEffectiveOnlineState() {
        _isOnline.value = _isDeviceOnline.value && !_isSimulatedOffline.value
    }
}
