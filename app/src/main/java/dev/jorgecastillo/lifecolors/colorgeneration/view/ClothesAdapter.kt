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
import kotlinx.android.synthetic.main.item_clothing.view.favButton

class ClothesAdapter(
  private val onItemClick: (ClothingItem) -> Unit = {},
  private val onItemFav: (ClothingItem) -> Unit = {}
) : ListAdapter<ClothingItem, ViewHolder>(ClothingItemDiffCallback()) {

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
      if (item.isPlaceHolder) {
        bindPlaceholder()
      } else {
        bindItem(item)
      }
    }

    private fun bindPlaceholder() {
      itemView.setOnClickListener(null)
      itemView.favButton.visibility = View.GONE

      val image = itemView.findViewById<ImageView>(R.id.image)
      image.alpha = 0.2f
      Picasso.get().load(R.drawable.clothing_placeholder).into(image)

      val price = itemView.findViewById<TextView>(R.id.price)
      price.visibility = View.GONE
    }

    private fun bindItem(item: ClothingItem) {
      itemView.setOnClickListener { onItemClick(item) }
      itemView.favButton.bindFavIcon(item.isFaved)
      itemView.favButton.setOnClickListener { onItemFav(item) }

      val image = itemView.findViewById<ImageView>(R.id.image)
      image.alpha = 1f
      Picasso.get().load(item.imageUrl).into(image)

      val price = itemView.findViewById<TextView>(R.id.price)
      price.visibility = View.VISIBLE
      price.text = item.price
    }

    private fun ImageButton.bindFavIcon(isFaved: Boolean?) {
      visibility = View.VISIBLE
      setImageResource(
        if (isFaved == true) {
          R.drawable.ic_favorite_dark
        } else {
          R.drawable.ic_favorite_border_dark
        }
      )
    }
  }
}
