package dev.jorgecastillo.lifecolors.colorgeneration.view.list

import androidx.recyclerview.widget.DiffUtil
import dev.jorgecastillo.zalandoclient.ZalandoItem

class ZalandoItemDiffCallback : DiffUtil.ItemCallback<ZalandoItem>() {
  override fun areItemsTheSame(oldItem: ZalandoItem, newItem: ZalandoItem): Boolean {
    return oldItem.name == newItem.name
  }

  override fun areContentsTheSame(oldItem: ZalandoItem, newItem: ZalandoItem): Boolean {
    return oldItem == newItem
  }
}
