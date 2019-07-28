package dev.jorgecastillo.lifecolors.common.view.list

import androidx.recyclerview.widget.DiffUtil
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState

class ColorViewStateDiffCallback : DiffUtil.ItemCallback<ColorViewState>() {
  override fun areItemsTheSame(oldItem: ColorViewState, newItem: ColorViewState): Boolean {
    return oldItem.color == newItem.color
  }

  override fun areContentsTheSame(oldItem: ColorViewState, newItem: ColorViewState): Boolean {
    return oldItem == newItem
  }
}
