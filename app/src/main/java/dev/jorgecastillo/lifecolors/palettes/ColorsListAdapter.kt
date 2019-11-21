package dev.jorgecastillo.lifecolors.palettes

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.jorgecastillo.androidcolorx.library.asCmyk
import dev.jorgecastillo.androidcolorx.library.asHex
import dev.jorgecastillo.androidcolorx.library.asRgb
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.common.view.list.ColorViewStateDiffCallback
import dev.jorgecastillo.lifecolors.common.view.menu.MenuItemProgressCircle
import dev.jorgecastillo.lifecolors.detail.view.Dot
import dev.jorgecastillo.lifecolors.palettes.ColorsListAdapter.ViewHolder
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState

class ColorsListAdapter(
    private val onItemClick: (View, ColorViewState, Int) -> Unit,
    private val onFavItemClick: (View, ColorViewState, Int) -> Unit
) : ListAdapter<ColorViewState, ViewHolder>(ColorViewStateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_color_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onFavItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            colorViewState: ColorViewState,
            onItemClick: (View, ColorViewState, Int) -> Unit,
            onFavItemClick: (View, ColorViewState, Int) -> Unit
        ) {
            val dotView = itemView.findViewById<Dot>(R.id.colorDot)
            dotView.transitionName = "${colorViewState.color}"
            dotView.color = colorViewState.color
            dotView.invalidate()

            val hexColor = colorViewState.color.asHex()
            val hexColorView = itemView.findViewById<TextView>(R.id.hexCode)
            hexColorView.text = hexColor.toString()

            val rgbColor = colorViewState.color.asRgb()
            val rgbColorView = itemView.findViewById<TextView>(R.id.rgb)
            rgbColorView.text = itemView.resources.getString(R.string.rgb, rgbColor)

            val cmykColorView = itemView.findViewById<TextView>(R.id.cmyk)
            cmykColorView.text =
                itemView.resources.getString(R.string.cmyk, colorViewState.color.asCmyk())

            itemView.setOnClickListener { onItemClick(dotView, colorViewState, adapterPosition) }

            val loading = itemView.findViewById<MenuItemProgressCircle>(R.id.loading)
            loading.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)

            val favButton = itemView.findViewById<ImageButton>(R.id.favButton)
            favButton.setImageResource(
                if (colorViewState.isFavorite) {
                    R.drawable.ic_favorite_white_24dp
                } else {
                    R.drawable.ic_favorite_border_white_24dp
                }
            )
            favButton.setOnClickListener {
                onFavItemClick(
                    dotView,
                    colorViewState,
                    adapterPosition
                )
            }

            if (colorViewState.isLoading) {
                loading.visibility = View.VISIBLE
                favButton.visibility = View.GONE
            } else {
                loading.visibility = View.GONE
                favButton.visibility = View.VISIBLE
            }
        }
    }
}
