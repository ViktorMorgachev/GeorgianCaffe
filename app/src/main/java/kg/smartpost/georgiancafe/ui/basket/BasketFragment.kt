package kg.smartpost.georgiancafe.ui.basket

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kg.smartpost.georgiancafe.data.local.basket.BasketOrder
import kg.smartpost.georgiancafe.data.network.NetworkResponse
import kg.smartpost.georgiancafe.databinding.FragmentBasketBinding
import kg.smartpost.georgiancafe.ui.utils.EventListener
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BasketFragment : Fragment() {

    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!
    private val basketViewModel by viewModels<BasketViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            createOrder.setOnClickListener {



                if (editAdress.text.isNotEmpty() && editName.text.isNotEmpty() && editPhone.text.isNotEmpty()){

                    lifecycleScope.launchWhenResumed {
                        basketViewModel.createOrder(basketViewModel.basket.data.value, userName = editName.text.toString(), userPhone = editPhone.text.toString(), userAddress = editAdress.text.toString()).collect { response->
                            when (response) {
                                is NetworkResponse.Success -> {
                                    Toast.makeText(requireContext(), "Оформить заказ: Success", Toast.LENGTH_LONG).show()
                                }

                                is NetworkResponse.Error -> {
                                    Toast.makeText(requireContext(), "Оформить заказ: Error", Toast.LENGTH_LONG).show()
                                }

                                is NetworkResponse.Loading -> {
                                    Toast.makeText(requireContext(), "Оформить заказ: Loading", Toast.LENGTH_LONG).show()
                                }
                            }

                        }
                    }

                } else {
                    lifecycleScope.launchWhenResumed {
                        basketViewModel.createOrder(basketViewModel.basket.data.value, userName = "Вася", userPhone = "+79210405416", userAddress = "Moskow").collect { response->
                            when (response) {
                                is NetworkResponse.Success -> {
                                    Toast.makeText(requireContext(), "Оформить заказ: Success", Toast.LENGTH_LONG).show()
                                }

                                is NetworkResponse.Error -> {
                                    Toast.makeText(requireContext(), "Оформить заказ: Error", Toast.LENGTH_LONG).show()
                                }

                                is NetworkResponse.Loading -> {
                                    Toast.makeText(requireContext(), "Оформить заказ: Loading", Toast.LENGTH_LONG).show()
                                }
                            }

                        }
                    }
                }
            }

        }
        lifecycleScope.launchWhenResumed {
            updateOrder()
        }
    }

    private fun updateOrder() {
        lifecycleScope.launchWhenResumed {
            basketViewModel.getDataInfo().collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        response.data?.let { data ->
                            basketViewModel.userDiscount = data.user.personal_discount
                            with(binding){
                                editAdress.hint = data.user.cart_address
                                editName.hint = data.user.cart_name
                                editPhone.hint = data.user.cart_phone
                                textCommonWithDiscount.text = "Co скидкой ${data.user.personal_discount}%"
                            }
                        }
                    }
                }
                basketViewModel.getOrders().collect {
                    val orders = it
                    calculateTotalPriceInfo()
                    with(binding) {
                        recyclerBasketOrders.layoutManager = LinearLayoutManager(this.root.context)
                        recyclerBasketOrders.adapter = BasketAdapter(
                            data = orders.toMutableList(),
                            onActionDeleteOrderItem = { basketOrder ->
                                lifecycleScope.launchWhenResumed {
                                    basketViewModel.deleteDish(basketOrder.dish).collect {
                                        updateOrder()
                                        calculateTotalPriceInfo()
                                    }
                                }
                            },
                            onActionAddDishAction = { actualCount, dish ->
                                lifecycleScope.launchWhenResumed {
                                    basketViewModel.addDish(actualCount, dish).collect {
                                        calculateTotalPriceInfo()
                                    }
                                }
                            })
                    }
                }
            }


        }
    }

    private fun calculateTotalPriceInfo() {
        with(binding){
            textCommonPriceOrderValue.text = "${basketViewModel.basket.getTotalPrice()} руб"
            val resultPrice = (basketViewModel.basket.getTotalPrice() - (basketViewModel.basket.getTotalPrice() / 100 * basketViewModel.userDiscount))
            textCommonWithDiscountValue.text = "${resultPrice} руб"
            textCommonPriceResultValue.text = "${resultPrice} руб"
        }
    }

}