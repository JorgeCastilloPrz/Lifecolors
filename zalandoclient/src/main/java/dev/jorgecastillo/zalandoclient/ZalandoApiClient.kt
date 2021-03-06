package dev.jorgecastillo.zalandoclient

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import dev.jorgecastillo.androidcolorx.library.HEXColor
import dev.jorgecastillo.androidcolorx.library.asColorInt
import org.jsoup.Jsoup
import java.lang.Math.pow
import kotlin.math.sqrt

data class ZalandoItem(
    val imageUrl: String,
    val url: String,
    val brandName: String,
    val articleName: String,
    val price: String,
    val category: ZalandoApiClient.ZalandoCategory
)

class ZalandoApiClient {

    sealed class ZalandoColor {
        companion object {
            fun all() = listOf(
                Negro(),
                Marron(),
                Beige(),
                Gris(),
                Blanco(),
                Azul(),
                VerdeAzulado(),
                Turquesa(),
                Verde(),
                VerdeOliva(),
                Amarillo(),
                Naranja(),
                Rojo(),
                Rosa(),
                Lila(),
                Dorado(),
                Plateado()
            )
        }

        abstract val name: String
        abstract val hex: String

        data class Negro(
            override val name: String = "_negro",
            override val hex: String = "000000"
        ) : ZalandoColor()

        data class Marron(
            override val name: String = "_marron",
            override val hex: String = "6F3E18"
        ) : ZalandoColor()

        data class Beige(
            override val name: String = "_beige",
            override val hex: String = "D4BE8D"
        ) : ZalandoColor()

        data class Gris(override val name: String = "_gris", override val hex: String = "838383") :
            ZalandoColor()

        data class Blanco(
            override val name: String = "_blanco",
            override val hex: String = "FFFFFF"
        ) : ZalandoColor()

        data class Azul(override val name: String = "_azul", override val hex: String = "0F73AD") :
            ZalandoColor()

        data class VerdeAzulado(
            override val name: String = "_plomizo",
            override val hex: String = "16738F"
        ) :
            ZalandoColor()

        data class Turquesa(
            override val name: String = "_turquesa",
            override val hex: String = "38CBCE"
        ) : ZalandoColor()

        data class Verde(
            override val name: String = "_verde",
            override val hex: String = "59D36C"
        ) : ZalandoColor()

        data class VerdeOliva(
            override val name: String = "_verdeoliva",
            override val hex: String = "548B13"
        ) :
            ZalandoColor()

        data class Amarillo(
            override val name: String = "_amarillo",
            override val hex: String = "FFDD34"
        ) : ZalandoColor()

        data class Naranja(
            override val name: String = "_naranja",
            override val hex: String = "FB853C"
        ) : ZalandoColor()

        data class Rojo(override val name: String = "_rojo", override val hex: String = "FF0000") :
            ZalandoColor()

        data class Rosa(override val name: String = "_rosa", override val hex: String = "F562B9") :
            ZalandoColor()

        data class Lila(override val name: String = "_lila", override val hex: String = "9349AA") :
            ZalandoColor()

        data class Dorado(
            override val name: String = "_dorado",
            override val hex: String = "F6D983"
        ) : ZalandoColor()

        data class Plateado(
            override val name: String = "_plateado",
            override val hex: String = "CBCBCB"
        ) : ZalandoColor()
    }

    sealed class ZalandoCategory {

        abstract val stringId: Int
        abstract val urlSection: String

        sealed class Mujer : ZalandoCategory() {

            companion object {
                fun all(): List<Mujer> = listOf(
                    CalzadoMujer(),
                    VestidosMujer(),
                    CamisetasYTopsMujer(),
                    CamisasYBlusasMujer(),
                    PantalonesMujer(),
                    VaquerosMujer(),
                    MonosMujer(),
                    FaldasMujer(),
                    ChaquetasPuntoYJerseysMujer(),
                    SandaliasMujer(),
                    ZapatillasMujer(),
                    AlpargatasMujer(),
                    ZapatosPlanosMujer(),
                    ZuecosMujer(),
                    BailarinasMujer(),
                    MocasinesMujer(),
                    BotinesMujer(),
                    BotasMujer(),
                    ZapatillasDeporteMujer(),
                    ZapatosTaconMujer(),
                    GafasDeSolMujer(),
                    BolsosMujer(),
                    MonederosMujer(),
                    GorrosYSombrerosMujer(),
                    BisuteriaMujer(),
                    CollaresMujer(),
                    CinturonesMujer(),
                    MochilasMujer(),
                    RelojesMujer(),
                    PanuelosYBufandas()
                )
            }

            // ropa
            data class RopaMujer(
                override val stringId: Int = R.string.ropa_mujer,
                override val urlSection: String = "ropa-de-mujer"
            ) : Mujer()

            data class CalzadoMujer(
                override val stringId: Int = R.string.calzado_mujer,
                override val urlSection: String = "calzado-de-mujer"
            ) : Mujer()

            data class VestidosMujer(
                override val stringId: Int = R.string.vestidos_mujer,
                override val urlSection: String = "ropa-de-mujer-vestidos"
            ) : Mujer()

            data class CamisetasYTopsMujer(
                override val stringId: Int = R.string.camisetas_y_tops_mujer,
                override val urlSection: String = "ropa-de-mujer-camisetas-y-tops"
            ) : Mujer()

            data class CamisasYBlusasMujer(
                override val stringId: Int = R.string.camisas_y_blusas_mujer,
                override val urlSection: String = "ropa-de-mujer-blusas-y-blusones"
            ) : Mujer()

            data class PantalonesMujer(
                override val stringId: Int = R.string.pantalones_mujer,
                override val urlSection: String = "pantalones-mujer"
            ) : Mujer()

            data class VaquerosMujer(
                override val stringId: Int = R.string.vaqueros_mujer,
                override val urlSection: String = "pantalones-vaqueros-mujer"
            ) : Mujer()

            data class MonosMujer(
                override val stringId: Int = R.string.monos_mujer,
                override val urlSection: String = "pantalones-monos-mujer"
            ) : Mujer()

            data class FaldasMujer(
                override val stringId: Int = R.string.faldas_mujer,
                override val urlSection: String = "faldas-mujer"
            ) : Mujer()

            data class ChaquetasPuntoYJerseysMujer(
                override val stringId: Int = R.string.chaquetas_y_jerseys,
                override val urlSection: String = "chaquetas-de-punto-y-jerseis-mujer"
            ) :
                Mujer()

            // calzado
            data class SandaliasMujer(
                override val stringId: Int = R.string.sandalias_mujer,
                override val urlSection: String = "sandalias-mujer"
            ) : Mujer()

            data class ZapatillasMujer(
                override val stringId: Int = R.string.zapatillas_mujer,
                override val urlSection: String = "zapatillas-mujer"
            ) : Mujer()

            data class AlpargatasMujer(
                override val stringId: Int = R.string.alpargatas_mujer,
                override val urlSection: String = "alpargatas-mujer"
            ) : Mujer()

            data class ZapatosPlanosMujer(
                override val stringId: Int = R.string.zapatos_planos_mujer,
                override val urlSection: String = "zapatos-bajos-mujer"
            ) : Mujer()

            data class ZuecosMujer(
                override val stringId: Int = R.string.zuecos,
                override val urlSection: String = "zuecos-mujer"
            ) : Mujer()

            data class BailarinasMujer(
                override val stringId: Int = R.string.bailarinas,
                override val urlSection: String = "bailarinas-mujer"
            ) : Mujer()

            data class MocasinesMujer(
                override val stringId: Int = R.string.mocasines_mujer,
                override val urlSection: String = "zapatos-bajos-mocasines-mujer"
            ) : Mujer()

            data class BotinesMujer(
                override val stringId: Int = R.string.botines_mujer,
                override val urlSection: String = "botines-mujer"
            ) : Mujer()

            data class BotasMujer(
                override val stringId: Int = R.string.botas_mujer,
                override val urlSection: String = "botas-mujer"
            ) : Mujer()

            data class ZapatillasDeporteMujer(
                override val stringId: Int = R.string.zapatillas_deporte_mujer,
                override val urlSection: String = "zapatillas-deporte-mujer"
            ) : Mujer()

            data class ZapatosTaconMujer(
                override val stringId: Int = R.string.zapatos_tacon,
                override val urlSection: String = "zapatos-altos-mujer"
            ) : Mujer()

            // complementos
            data class GafasDeSolMujer(
                override val stringId: Int = R.string.gafas_sol_mujer,
                override val urlSection: String = "gafas-sol-complementos-mujer"
            ) : Mujer()

            data class BolsosMujer(
                override val stringId: Int = R.string.bolsos_mujer,
                override val urlSection: String = "bolsos-maletas-complementos-mujer"
            ) : Mujer()

            data class MonederosMujer(
                override val stringId: Int = R.string.monederos_mujer,
                override val urlSection: String = "monederos-complementos-mujer"
            ) : Mujer()

            data class GorrosYSombrerosMujer(
                override val stringId: Int = R.string.gorros_sombreros_mujer,
                override val urlSection: String = "gorros-sombreros-gorras-complementos-mujer"
            ) :
                Mujer()

            data class BisuteriaMujer(
                override val stringId: Int = R.string.bisuteria_mujer,
                override val urlSection: String = "bisuteria-relojes-complementos-mujer"
            ) : Mujer()

            data class CollaresMujer(
                override val stringId: Int = R.string.collares_mujer,
                override val urlSection: String = "collares"
            ) : Mujer()

            data class CinturonesMujer(
                override val stringId: Int = R.string.cinturones_mujer,
                override val urlSection: String = "cinturones-complementos-mujer"
            ) : Mujer()

            data class MochilasMujer(
                override val stringId: Int = R.string.mochilas_mujer,
                override val urlSection: String = "mochilas-complementos-mujer"
            ) : Mujer()

            data class RelojesMujer(
                override val stringId: Int = R.string.relojes_mujer,
                override val urlSection: String = "relojes-mujer"
            ) : Mujer()

            data class PanuelosYBufandas(
                override val stringId: Int = R.string.panuelos_bufandas_mujer,
                override val urlSection: String = "panuelos-mujer"
            ) : Mujer()
        }
    }

    /**
     * Url format: https://www.zalando.es/zapatillas-hombre/_plateado/
     */
    fun get(category: ZalandoCategory, color: ZalandoColor): List<ZalandoItem> {
        try {
            val url = "https://www.zalando.es/${category.urlSection}/${color.name}/"
            val doc = Jsoup.connect(url).get()
            println(doc.title())
            val itemsGridDiv = doc.body().select("z-grid.cat_catArticles-2Pxh7")
            return itemsGridDiv[0].children().fold(listOf()) { acc, gridItem ->

                val imageItems = gridItem.select("[class^=cat_image-]")
                if (imageItems.isNotEmpty()) {
                    val imageUrl = gridItem.select("[class^=cat_image-]").first().attr("src")
                    val url =
                        "https://www.zalando.es${gridItem.select("[class^=cat_infoDetail]").first()
                            .attr("href")}"
                    val brandName = gridItem.select("[class^=cat_brandName]").first().text()
                    val articleName = gridItem.select("[class^=cat_articleName]").first().text()

                    val originalPrice = gridItem.select("[class^=cat_originalPrice]")
                    val promotionalPriceItems = gridItem.select("[class^=cat_promotionalPrice]")

                    val price = if (promotionalPriceItems.isNotEmpty()) {
                        promotionalPriceItems.first().text()
                    } else {
                        originalPrice.text()
                    }

                    acc + ZalandoItem(imageUrl, url, brandName, articleName, price, category)
                } else {
                    acc
                }
            }
        } catch (e: Exception) {
            return listOf()
        }
    }

    /**
     * Assumes the String is a HEX color with the format #FFFFFF or FFFFFF
     */
    private fun String.toColorInt(): Int {
        return if (this.startsWith("#")) {
            Color.parseColor(this)
        } else {
            Color.parseColor("#${this}")
        }
    }

    private fun Int.distanceTo(@ColorInt target: Int): Double {
        val colorHSL = FloatArray(3)
        ColorUtils.colorToHSL(target, colorHSL)
        colorHSL[2] = 0.5f
        val targetColorAdjusted = ColorUtils.HSLToColor(colorHSL)

        val r = (Color.red(this) + Color.red(targetColorAdjusted)) / 2f

        val deltaR = Color.red(this).toDouble() - Color.red(targetColorAdjusted)
        val deltaG = Color.green(this).toDouble() - Color.green(targetColorAdjusted)
        val deltaB = Color.blue(this).toDouble() - Color.blue(targetColorAdjusted)

        return sqrt(
            (2 + r / 256) * pow(deltaR, 2.0) + 4 * pow(
                deltaG,
                2.0
            ) + (2 + (255 - r) / 256) * pow(deltaB, 2.0)
        )
    }

    fun get(category: ZalandoCategory, source: HEXColor): List<ZalandoItem> {
        val closestColor = ZalandoColor.all().reduce { acc, currentColor ->
            if (acc.hex.toColorInt().distanceTo(source.asColorInt()) < currentColor.hex.toColorInt()
                    .distanceTo(source.asColorInt())
            ) {
                acc
            } else {
                currentColor
            }
        }
        return get(category, closestColor)
    }
}
