package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.colorgeneration.view.GeneratedColorsAdapter.ViewHolder
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorDetails

class GeneratedColorsAdapter(private val onItemClick: (View, ColorDetails, Int) -> Unit) :
  RecyclerView.Adapter<ViewHolder>() {

  var colors: List<ColorDetails> = listOf()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_generated_color_list, parent, false)
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
      val colorSquare = itemView.findViewById<View>(R.id.colorSquare)
      colorSquare.setBackgroundColor(colorDetails.color)

      val colorText = itemView.findViewById<TextView>(R.id.colorText)
      val hexColor = String.format("#%06X", 0xFFFFFF and colorDetails.color)
      colorText.text = hexColor

      itemView.setOnClickListener { onItemClick(colorText, colorDetails, position) }
    }
  }
}
