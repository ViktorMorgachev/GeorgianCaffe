package kg.smartpost.georgiancafe.data.network.create_order.repo

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kg.smartpost.georgiancafe.data.local.basket.BasketOrder
import kg.smartpost.georgiancafe.data.network.NetworkDataSource
import kg.smartpost.georgiancafe.data.network.NetworkResponse
import kg.smartpost.georgiancafe.data.network.create_order.CreateOrder
import kg.smartpost.georgiancafe.data.network.create_order.response.ResponseFromServer
import kg.smartpost.georgiancafe.utils.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class CreateOrderRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource
) : BaseRepository() {

    suspend fun createOrder(orders: List<BasketOrder>, userName: String, userPhone: String, userAddress: String): Flow<NetworkResponse<ResponseFromServer>> {
        val arrays = mutableListOf<CreateOrder.Order>()
        orders.forEach {
            arrays.add(CreateOrder.Order(count = it.count, id = it.dish.id.toInt()))
        }
        val order  = CreateOrder(address =  userAddress, client_name = userName, client_phone = userPhone, type = 1, card = arrays)
        return flow<NetworkResponse<ResponseFromServer>> {
            emit(safeApiCall { networkDataSource.createOrder(order) })
        }.flowOn(Dispatchers.IO)
    }

}