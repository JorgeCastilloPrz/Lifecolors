package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.lifecolors.colorgeneration.view.ClothesAdapter.ViewHolder
import dev.jorgecastillo.lifecolors.colorgeneration.view.list.ClothingItemDiffCallback
import kotlinx.android.synthetic.main.item_clothing.view.*

class ClothesAdapter(
    private val onItemClick: (ClothingItem) -> Unit = {},
    private val onItemFav: (ClothingItem) -> Unit = {}
) :
    ListAdapter<ClothingItem, ViewHolder>(ClothingItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_clothing, parent, false)
        return ViewHolder(view, onItemClick, onItemFav)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onItemClick: (ClothingItem) -> Unit,
        private val onItemFav: (ClothingItem) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(item: ClothingItem) {
            itemView.setOnClickListener { onItemClick(item) }
            itemView.favButton.bindFavIcon(item.isFaved)
            itemView.favButton.setOnClickListener { onItemFav(item) }

            val image = itemView.findViewById<ImageView>(R.id.image)
            Picasso.get().load(item.imageUrl).into(image)

            val price = itemView.findViewById<TextView>(R.id.price)
            price.text = item.price
        }

        private fun ImageButton.bindFavIcon(isFaved: Boolean?) {
            setImageResource(
                if (isFaved == true) {
                    R.drawable.ic_favorite_black_24dp
                } else {
                    R.drawable.ic_favorite_border_black_24dp
                }
            )
        }
    }
}
