package kg.smartpost.georgiancafe.data.network

import com.google.gson.Gson
import kg.smartpost.georgiancafe.data.network.create_order.CreateOrder
import retrofit2.Response
import javax.inject.Inject

class NetworkDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun getData(page: String) =
        apiService.getData(page)

    suspend fun getCategory(page: String) =
        apiService.getCategory(page)

    suspend fun getDishes(page: String) =
        apiService.getDishes(page)

    suspend fun createOrder(createOrder: CreateOrder): Response<Any> {
        val jsonString = Gson().toJson(createOrder)
        return apiService.createOrder(requestData = jsonString)
    }

}