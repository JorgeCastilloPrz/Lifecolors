package dev.jorgecastillo.lifecolors.colorgeneration.view.list

import androidx.recyclerview.widget.DiffUtil
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem

class ClothingItemDiffCallback : DiffUtil.ItemCallback<ClothingItem>() {
    override fun areItemsTheSame(oldItem: ClothingItem, newItem: ClothingItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: ClothingItem, newItem: ClothingItem): Boolean {
        return oldItem == newItem
    }
}
