package kg.smartpost.georgiancafe.data.network

import kg.smartpost.georgiancafe.data.network.category.model.ModelCategory
import kg.smartpost.georgiancafe.data.network.dishes.model.ModelDishes
import kg.smartpost.georgiancafe.data.network.home.model.ModelData
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api.php")
    suspend fun getData(
        @Query("page") page: String,
    ): Response<ModelData>


    @POST("api.php")
    suspend fun createOrder(
        @Body requestData: String
    ): Response<Any>

    @GET("api.php")
    suspend fun getCategory(
        @Query("page") page: String,
    ): Response<ModelCategory>

    @GET("api.php")
    suspend fun getDishes(
        @Query("page") page: String,
    ): Response<ModelDishes>

}