package kg.smartpost.georgiancafe.data.network.create_order

import com.google.gson.annotations.SerializedName

data class CreateOrder(
    val address: String = "",
    @SerializedName("card")
    val card: List<Order> = listOf(),
    val client_name: String = "",
    val client_phone: String = "",
    val type: Int = 0
) {
    data class Order(
        @SerializedName("count")
        val count: Int = 0,
        @SerializedName("id")
        val id: Int = 0
    )
}