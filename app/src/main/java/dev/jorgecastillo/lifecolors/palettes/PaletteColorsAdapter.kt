package dev.jorgecastillo.lifecolors.palettes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.common.view.extensions.toCMYK
import dev.jorgecastillo.lifecolors.detail.view.Dot
import dev.jorgecastillo.lifecolors.palettes.PaletteColorsAdapter.ViewHolder
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorDetails

class PaletteColorsAdapter(private val onItemClick: (View, ColorDetails, Int) -> Unit) :
  RecyclerView.Adapter<ViewHolder>() {

  var colors: List<ColorDetails> = listOf()
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
    holder.bind(colors[position], onItemClick, position)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
      colorDetails: ColorDetails,
      onItemClick: (View, ColorDetails, Int) -> Unit,
      position: Int
    ) {
      val dotView = itemView.findViewById<Dot>(R.id.colorDot)
      dotView.transitionName = "${colorDetails.color}$position"
      dotView.color = colorDetails.color
      dotView.invalidate()

      val hexColor = String.format("#%06X", 0xFFFFFF and colorDetails.color)
      val hexColorView = itemView.findViewById<TextView>(R.id.hexCode)
      hexColorView.text = hexColor

      val rgbColor =
        "${Color.red(colorDetails.color)} / ${Color.green(colorDetails.color)} / ${Color.blue(colorDetails.color)}"
      val rgbColorView = itemView.findViewById<TextView>(R.id.rgb)
      rgbColorView.text = itemView.resources.getString(R.string.rgb, rgbColor)

      val cmykColorView = itemView.findViewById<TextView>(R.id.cmyk)
      cmykColorView.text = itemView.resources.getString(R.string.cmyk, colorDetails.color.toCMYK())

      itemView.setOnClickListener { onItemClick(dotView, colorDetails, position) }
    }
  }
}
