package kg.smartpost.georgiancafe.data.local.basket

import kg.smartpost.georgiancafe.data.network.dishes.model.ModelDishes

data class BasketOrder(val dish: ModelDishes.CatDish.Dishes, val count: Int)
