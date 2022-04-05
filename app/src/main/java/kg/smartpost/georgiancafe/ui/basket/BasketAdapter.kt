package kg.smartpost.georgiancafe.ui.basket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kg.smartpost.georgiancafe.data.local.basket.BasketOrder
import kg.smartpost.georgiancafe.data.network.dishes.model.ModelDishes
import kg.smartpost.georgiancafe.databinding.BasketItemBinding
import kg.smartpost.georgiancafe.utils.API_BASE_URL
import kotlin.text.StringBuilder

class BasketAdapter(val data: MutableList<BasketOrder>, val onActionDeleteOrderItem: (BasketOrder)->Unit, val onActionAddDishAction: (Int, ModelDishes.CatDish.Dishes)->Unit): RecyclerView.Adapter<BasketAdapter.BasketHolder>() {

    init {
        data.sortedBy { it.dish.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketHolder {
        val itemBinding = BasketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BasketHolder, position: Int) {
        val data = data[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = data.size

   inner class BasketHolder(private val itemBinding: BasketItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(order: BasketOrder) {
            val dish = order.dish
            val count = order.count
            with(itemBinding){

                Glide.with(itemBinding.root).load("${API_BASE_URL}admin/${dish.link_photo}.jpg")
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                    .into(dishImage)
                dishDelete.setOnClickListener {
                    onActionDeleteOrderItem.invoke(order)
                }
                dishName.text = dish.name
                itemCounter.txtTotal.text = "$count"
                itemCounter.btnPlus.setOnClickListener {
                    var total = itemCounter.txtTotal.text.toString().toInt()
                    total += if (total == 0) {
                        dish.min.toInt()
                    } else {
                        1
                    }
                    itemCounter.txtTotal.text = total.toString()
                    onActionAddDishAction.invoke(total, dish)
                    calculatePriceForItem(total, dish)
                }

                itemCounter.btnMinus.setOnClickListener {
                    var total = itemCounter.txtTotal.text.toString().toInt()
                    if (total == dish.min.toInt()) {
                        total = 0
                    } else {
                        if (total != 0) {
                            total -= 1
                        }
                    }
                    itemCounter.txtTotal.text = total.toString()
                    onActionAddDishAction.invoke(total, dish)
                    calculatePriceForItem(total, dish)
                }
                calculatePriceForItem(count, dish)

            }
        }

       private fun calculatePriceForItem(count: Int, dish: ModelDishes.CatDish.Dishes) {
           val mainPriceTextInfo = StringBuilder()
           mainPriceTextInfo.append("${dish.cost.toInt() * count} руб.")
           if (dish.hasPricesForOneItem()){
               mainPriceTextInfo.append("(${dish.cost}р./шт)")
           }
           with(itemBinding){
               dishMainInformation.text = mainPriceTextInfo
           }

       }

   }
}