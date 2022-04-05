package kg.smartpost.georgiancafe.ui.basket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.smartpost.georgiancafe.data.local.basket.Basket
import kg.smartpost.georgiancafe.data.local.basket.BasketOrder
import kg.smartpost.georgiancafe.data.network.NetworkResponse
import kg.smartpost.georgiancafe.data.network.dishes.model.ModelDishes
import kg.smartpost.georgiancafe.data.network.home.model.ModelData
import kg.smartpost.georgiancafe.data.network.home.repo.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor
    (
    val basket: Basket,
    private val dataRepository: DataRepository,
    application: Application
) : AndroidViewModel(application) {

   var userDiscount: Int = 0

    fun getOrders() = flow<List<BasketOrder>> {
        emit(basket.data.value)
    }

    suspend fun getDataInfo(): Flow<NetworkResponse<ModelData>> {
       return dataRepository.getData("start")
    }

    fun clearBasket() = basket.clearBasket()

    fun addDish(dishCount: Int, dish: ModelDishes.CatDish.Dishes) = basket.addDish(dishCount, dish)

    fun deleteDish(dish: ModelDishes.CatDish.Dishes) = basket.deleteDish(dish)

    fun createOrder(order: List<BasketOrder>, userName: String, userPhone: String, userAddress: String) = flow<Boolean>{

    }
}