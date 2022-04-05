package kg.smartpost.georgiancafe.data.network.create_order

data class CreateOrder(
    val address: String = "",
    val array: List<Order> = listOf(),
    val client_name: String = "",
    val client_phone: String = "",
    val type: Int = 0
) {
    data class Order(
        val count: Int = 0,
        val id: Int = 0
    )
}