package com.example.benakmoume_yahi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.models.Restaurant
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RestaurantViewModel : ViewModel() {
    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    fun loadRestaurants() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRestaurants()
                _restaurants.value = response.body() ?: emptyList()
            } catch (e: Exception) {
                _restaurants.value = emptyList()
            }
        }
    }
}