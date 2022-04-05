package kg.smartpost.georgiancafe.data.local.basket

import android.util.Log
import kg.smartpost.georgiancafe.data.network.dishes.model.ModelDishes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Basket @Inject constructor() {

    val data: MutableStateFlow<MutableList<BasketOrder>> = MutableStateFlow(mutableListOf())

    fun addDish( actualCount: Int, dish: ModelDishes.CatDish.Dishes) = flow<Int>{
        val oldData = mutableListOf<BasketOrder>().also { it.addAll(data.value) }
        if (oldData.map { it.dish.id }.contains(dish.id)){
            val dataForRemove = oldData.first { it.dish.id == dish.id }
            oldData.remove(dataForRemove)
        }
        if (actualCount != 0){
            val basketOrder = BasketOrder(dish, count = actualCount)
            oldData.add(basketOrder)
        }
        data.value = oldData
        emit(oldData.count())
    }

    fun getTotalPrice(): Int {
        val baseInfo = StringBuilder().append("\n")
        data.value.forEach {
            baseInfo.append("Dish ${it.dish.name} Count ${it.count} DishPrice: ${it.dish.cost} Price ${it.count * it.dish.cost.toInt()}\n")
        }
        Log.d("Basket", "getTotalPrice() basketItems $baseInfo")
        var priceResult = 0
        data.value.forEach {
            priceResult += it.count * it.dish.cost.toInt()
        }
        return priceResult
    }

    fun deleteDish(dish: ModelDishes.CatDish.Dishes) = flow<Int>{
        val oldData = mutableListOf<BasketOrder>().also { it.addAll(data.value) }
        if (oldData.map { it.dish.id }.contains(dish.id)){
            val dataForRemove = oldData.first { it.dish.id == dish.id }
            oldData.remove(dataForRemove)
        }
        data.value = oldData
        emit(oldData.count())
    }


    fun clearBasket() {
        data.value = mutableListOf()
    }
}

