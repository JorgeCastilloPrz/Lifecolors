package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.colorgeneration.view.ClothesAdapter.ViewHolder
import dev.jorgecastillo.lifecolors.colorgeneration.view.listadapter.ZalandoItemDiffCallback
import dev.jorgecastillo.zalandoclient.ZalandoItem

class ClothesAdapter(private val onItemClick: (ZalandoItem) -> Unit = {}) : ListAdapter<ZalandoItem, ViewHolder>(ZalandoItemDiffCallback()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clothing, parent, false)
    return ViewHolder(view, onItemClick)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  class ViewHolder(itemView: View, val onItemClick: (ZalandoItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: ZalandoItem) {
      itemView.setOnClickListener { onItemClick(item) }

      val image = itemView.findViewById<ImageView>(R.id.image)
      Picasso.get().load(item.imageUrl).into(image)
    }
  }
}
