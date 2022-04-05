package kg.smartpost.georgiancafe.data.network.basket

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.util.HashMap
import javax.inject.Inject
import javax.inject.Singleton

// usersOrders dishID: Int, count:  Int

data class BasketOrder(val nameOrder: String, val price: Int, val id: Int, val count: Int, val priceForOneItem: Int? = null)

@Singleton
class Basket @Inject constructor() {

    val data: MutableStateFlow<MutableList<BasketOrder>> = MutableStateFlow(mutableListOf())

    fun addDish(dishID: Int, actualCount: Int, price: Int, priceForOneItem: Int? = null, dishName: String) = flow<Int>{
        val oldData = mutableListOf<BasketOrder>().also { it.addAll(data.value) }
        if (oldData.map { it.id }.contains(dishID)){
            val dataForRemove = oldData.first { it.id == dishID }
            oldData.remove(dataForRemove)
        }
        if (actualCount != 0){
            val basketOrder = BasketOrder(id = dishID, nameOrder = dishName, price = price, priceForOneItem = priceForOneItem, count = actualCount)
            oldData.add(basketOrder)
        }
        emit(oldData.count())
        data.value = oldData
    }

    fun clearBasket() {
        data.value = mutableListOf()
    }
}

