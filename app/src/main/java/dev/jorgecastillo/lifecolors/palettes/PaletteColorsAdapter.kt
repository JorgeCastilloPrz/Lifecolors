package dev.jorgecastillo.lifecolors.palettes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.colorgeneration.view.toCMYK
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHex
import dev.jorgecastillo.lifecolors.colorgeneration.view.toRGB
import dev.jorgecastillo.lifecolors.detail.view.Dot
import dev.jorgecastillo.lifecolors.palettes.PaletteColorsAdapter.ViewHolder
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState

class PaletteColorsAdapter(
  private val onItemClick: (View, ColorViewState, Int) -> Unit,
  private val onFavItemClick: (View, ColorViewState, Int) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

  var colors: List<ColorViewState> = listOf()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color_list, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount(): Int = colors.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(colors[position], onItemClick, onFavItemClick, position)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
      colorViewState: ColorViewState,
      onItemClick: (View, ColorViewState, Int) -> Unit,
      onFavItemClick: (View, ColorViewState, Int) -> Unit,
      position: Int
    ) {
      val dotView = itemView.findViewById<Dot>(R.id.colorDot)
      dotView.transitionName = "${colorViewState.color}$position"
      dotView.color = colorViewState.color
      dotView.invalidate()

      val hexColor = colorViewState.color.toHex()
      val hexColorView = itemView.findViewById<TextView>(R.id.hexCode)
      hexColorView.text = hexColor

      val rgbColor = colorViewState.color.toRGB()
      val rgbColorView = itemView.findViewById<TextView>(R.id.rgb)
      rgbColorView.text = itemView.resources.getString(R.string.rgb, rgbColor)

      val cmykColorView = itemView.findViewById<TextView>(R.id.cmyk)
      cmykColorView.text = itemView.resources.getString(R.string.cmyk, colorViewState.color.toCMYK())

      itemView.setOnClickListener { onItemClick(dotView, colorViewState, position) }

      val favButton = itemView.findViewById<ImageButton>(R.id.favButton)
      favButton.setImageResource(
        if (colorViewState.isFavorite) {
          R.drawable.ic_favorite_white_24dp
        } else {
          R.drawable.ic_favorite_border_white_24dp
        }
      )
      favButton.setOnClickListener { onFavItemClick(dotView, colorViewState, position) }
    }
  }
}
