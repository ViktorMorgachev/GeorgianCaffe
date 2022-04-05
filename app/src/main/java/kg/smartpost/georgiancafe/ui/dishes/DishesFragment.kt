package kg.smartpost.georgiancafe.ui.dishes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint
import kg.smartpost.georgiancafe.data.local.UserPreferencesViewModel
import kg.smartpost.georgiancafe.data.local.basket.Basket
import kg.smartpost.georgiancafe.data.network.NetworkResponse
import kg.smartpost.georgiancafe.data.network.dishes.model.ModelDishes
import kg.smartpost.georgiancafe.databinding.FragmentDishesBinding
import kg.smartpost.georgiancafe.ui.category.viewmodels.CategoryViewModel
import kg.smartpost.georgiancafe.ui.dishes.utils.CategoryRecyclerViewAdapter
import kg.smartpost.georgiancafe.ui.dishes.utils.DishesRecyclerViewAdapter
import kg.smartpost.georgiancafe.ui.dishes.viewmodels.DishesViewModel
import kg.smartpost.georgiancafe.utils.MyState
import kg.smartpost.georgiancafe.utils.NetworkStatusTracker
import kg.smartpost.georgiancafe.utils.NetworkStatusViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class DishesFragment : Fragment(), CategoryRecyclerViewAdapter.CategoryClickListener {


    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val dishesViewModel by viewModels<DishesViewModel>()
    private var _binding: FragmentDishesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var basket: Basket

    private val page = "menu"

    var dishes = mutableListOf<ModelDishes.CatDish.Dishes>()

    private var hochu_cat: Int? = -1

    private val viewModel: NetworkStatusViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val networkStatusTracker = NetworkStatusTracker(requireContext())
                    return NetworkStatusViewModel(networkStatusTracker) as T
                }
            },
        ).get(NetworkStatusViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            hochu_cat = arguments?.getInt("hochu_cat")
        }

        viewModel.state.observe(this) { state ->
            when (state) {
                MyState.Fetched -> getDishes()
                MyState.Error -> Toast.makeText(
                    requireContext(),
                    "Нет подключения к интернету!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        binding.swipeRefresh.setOnRefreshListener {
            viewModel.state.observe(this) { state ->
                when (state) {
                    MyState.Fetched -> getDishes()
                    MyState.Error -> Toast.makeText(
                        requireContext(),
                        "Нет подключения к интернету!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun getDishes() {
        dishesViewModel.getDishes(page)
        dishesViewModel.dishes.observe(this) { dishes ->
            when (dishes) {
                is NetworkResponse.Success -> {
                    dishes.data?.let { dishes ->
                        this.dishes.clear()
                        this.dishes.addAll(dishes.cat_dish.dishes)
                        val categoryAdapter = CategoryRecyclerViewAdapter(hochu_cat, this)
                        val layoutManager = FlexboxLayoutManager(requireContext())
                        layoutManager.justifyContent = JustifyContent.CENTER
                        layoutManager.alignItems = AlignItems.CENTER
                        layoutManager.flexDirection = FlexDirection.ROW
                        layoutManager.flexWrap = FlexWrap.WRAP
                        binding.categoryList.layoutManager = layoutManager
                        binding.categoryList.adapter = categoryAdapter
                        categoryAdapter.submitList(dishes.cat_dish.category)

                        val dishesAdapter = DishesRecyclerViewAdapter(basket){  dishCount, dish ->
                            lifecycleScope.launchWhenResumed {
                                dishesViewModel.addDish(dishCount, dish ).collect()
                            }
                        }
                        binding.dishesList.adapter = dishesAdapter
                        dishesAdapter.submitList(dishes.cat_dish.dishes.filter { it.id_menu.toInt()==hochu_cat})
                        binding.swipeRefresh.isRefreshing = false

                    }
                }

                is NetworkResponse.Error -> {
                    Toast.makeText(
                        requireContext(),
                        dishes.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResponse.Loading -> {
                }
            }
        }
    }

    override fun onCategoryClick(position: Int, id: Int) {
        hochu_cat = id
        val adapter = DishesRecyclerViewAdapter(basket){  dishCount, dish ->
            lifecycleScope.launchWhenResumed {
                dishesViewModel.addDish(dishCount, dish ).collect()
            }
        }
        binding.dishesList.adapter = adapter
        adapter.submitList(dishes.filter { it.id_menu.toInt()==id})
    }
}