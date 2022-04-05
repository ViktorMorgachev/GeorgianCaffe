package kg.smartpost.georgiancafe.ui.basket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kg.smartpost.georgiancafe.data.local.basket.Basket
import kg.smartpost.georgiancafe.data.local.basket.BasketOrder
import kg.smartpost.georgiancafe.data.network.NetworkResponse
import kg.smartpost.georgiancafe.data.network.create_order.repo.CreateOrderRepository
import kg.smartpost.georgiancafe.data.network.create_order.response.ResponseFromServer
import kg.smartpost.georgiancafe.data.network.dishes.model.ModelDishes
import kg.smartpost.georgiancafe.data.network.home.model.ModelData
import kg.smartpost.georgiancafe.data.network.home.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor
    (
    val basket: Basket,
    private val dataRepository: DataRepository,
    private val createOrderRepository: CreateOrderRepository,
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

    suspend fun createOrder(orders: List<BasketOrder>, userName: String, userPhone: String, userAddress: String) : Flow<NetworkResponse<ResponseFromServer>>{
        return createOrderRepository.createOrder(orders, userName, userPhone, userAddress)
    }

}