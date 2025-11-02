package com.example.benakmoume_yahi.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.models.Restaurant
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// États possibles de l'UI
sealed class RestaurantUiState {
    object Loading : RestaurantUiState()
    data class Success(val restaurant: Restaurant) : RestaurantUiState()
    data class Error(val message: String) : RestaurantUiState()
}

class RestaurantDetailViewModel : ViewModel() {

    // État observable par l'UI
    private val _uiState = MutableStateFlow<RestaurantUiState>(RestaurantUiState.Loading)
    val uiState: StateFlow<RestaurantUiState> = _uiState.asStateFlow()

    // Charger un restaurant par ID
    fun loadRestaurantById(restaurantId: Int) {
        viewModelScope.launch {
            _uiState.value = RestaurantUiState.Loading
            try {
                val response = RetrofitInstance.api.getRestaurantById(restaurantId)
                if (response.isSuccessful && response.body() != null) {
                    val restaurant = response.body()!!
                    // Transformer cuisineType et description en tags
                    /*val restaurantWithTags = restaurant.copy(
                        tags = listOfNotNull(
                            restaurant.cuisineType,
                            restaurant.description
                        )
                    )*/
                    _uiState.value = RestaurantUiState.Success(restaurant)
                } else {
                    _uiState.value = RestaurantUiState.Error(
                        "Restaurant non trouvé (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = RestaurantUiState.Error(
                    message = e.message ?: "Erreur de connexion"
                )
            }
        }
    }


}
