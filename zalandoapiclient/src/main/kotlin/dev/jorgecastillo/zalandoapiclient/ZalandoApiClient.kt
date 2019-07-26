package dev.jorgecastillo.zalandoapiclient

import dev.jorgecastillo.zalandoapiclient.ZalandoApiClient.ZalandoCategory
import dev.jorgecastillo.zalandoapiclient.ZalandoApiClient.ZalandoColor
import org.jsoup.Jsoup
import java.awt.Color
import kotlin.math.abs
import kotlin.math.sqrt

data class ZalandoItem(
  val imageUrl: String,
  val url: String,
  val name: String,
  val price: String
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

    data class Negro(override val name: String = "_negro", override val hex: String = "000000") : ZalandoColor()
    data class Marron(override val name: String = "_marron", override val hex: String = "6F3E18") : ZalandoColor()
    data class Beige(override val name: String = "_beige", override val hex: String = "D4BE8D") : ZalandoColor()
    data class Gris(override val name: String = "_gris", override val hex: String = "838383") : ZalandoColor()
    data class Blanco(override val name: String = "_blanco", override val hex: String = "FFFFFF") : ZalandoColor()
    data class Azul(override val name: String = "_azul", override val hex: String = "0F73AD") : ZalandoColor()
    data class VerdeAzulado(override val name: String = "_plomizo", override val hex: String = "16738F") :
      ZalandoColor()

    data class Turquesa(override val name: String = "_turquesa", override val hex: String = "38CBCE") : ZalandoColor()
    data class Verde(override val name: String = "_verde", override val hex: String = "59D36C") : ZalandoColor()
    data class VerdeOliva(override val name: String = "_verdeoliva", override val hex: String = "548B13") :
      ZalandoColor()

    data class Amarillo(override val name: String = "_amarillo", override val hex: String = "FFDD34") : ZalandoColor()
    data class Naranja(override val name: String = "_naranja", override val hex: String = "FB853C") : ZalandoColor()
    data class Rojo(override val name: String = "_rojo", override val hex: String = "FF0000") : ZalandoColor()
    data class Rosa(override val name: String = "_rosa", override val hex: String = "F562B9") : ZalandoColor()
    data class Lila(override val name: String = "_lila", override val hex: String = "9349AA") : ZalandoColor()
    data class Dorado(override val name: String = "_dorado", override val hex: String = "F6D983") : ZalandoColor()
    data class Plateado(override val name: String = "_plateado", override val hex: String = "CBCBCB") : ZalandoColor()
  }

  sealed class ZalandoCategory {

    abstract val urlSection: String

    sealed class Mujer : ZalandoCategory() {

      // ropa
      data class CalzadoMujer(override val urlSection: String = "calzado-de-mujer") : Mujer()

      data class VestidosMujer(override val urlSection: String = "ropa-de-mujer-vestidos") : Mujer()
      data class CamisetasYTopsMujer(override val urlSection: String = "ropa-de-mujer-camisetas-y-tops") : Mujer()
      data class CamisasYBlusasMujer(override val urlSection: String = "ropa-de-mujer-blusas-y-blusones") : Mujer()
      data class PantalonesMujer(override val urlSection: String = "pantalones-mujer") : Mujer()
      data class VaquerosMujer(override val urlSection: String = "pantalones-vaqueros-mujer") : Mujer()
      data class MonosMujer(override val urlSection: String = "pantalones-monos-mujer") : Mujer()
      data class FaldasMujer(override val urlSection: String = "faldas-mujer") : Mujer()
      data class ChaquetasPuntoYJerseysMujer(override val urlSection: String = "chaquetas-de-punto-y-jerseis-mujer") :
        Mujer()

      // calzado
      data class SandaliasMujer(override val urlSection: String = "sandalias-mujer") : Mujer()

      data class ZapatillasMujer(override val urlSection: String = "zapatillas-mujer") : Mujer()
      data class AlpargatasMujer(override val urlSection: String = "alpargatas-mujer") : Mujer()
      data class ZapatosPlanosMujer(override val urlSection: String = "zapatos-bajos-mujer") : Mujer()
      data class ZuecosMujer(override val urlSection: String = "zuecos-mujer") : Mujer()
      data class BailarinasMujer(override val urlSection: String = "bailarinas-mujer") : Mujer()
      data class MocasinesMujer(override val urlSection: String = "zapatos-bajos-mocasines-mujer") : Mujer()
      data class BotinesMujer(override val urlSection: String = "botines-mujer") : Mujer()
      data class BotasMujer(override val urlSection: String = "botas-mujer") : Mujer()
      data class ZapatillasDeporteMujer(override val urlSection: String = "zapatillas-deporte-mujer") : Mujer()
      data class ZapatosTaconMujer(override val urlSection: String = "zapatos-altos-mujer") : Mujer()

      // complementos
      data class GafasDeSolMujer(override val urlSection: String = "gafas-sol-complementos-mujer") : Mujer()

      data class BolsosMujer(override val urlSection: String = "bolsos-maletas-complementos-mujer") : Mujer()
      data class MonederosMujer(override val urlSection: String = "monederos-complementos-mujer") : Mujer()
      data class GorrosYSombrerosMujer(override val urlSection: String = "gorros-sombreros-gorras-complementos-mujer") :
        Mujer()

      data class BisuteriaMujer(override val urlSection: String = "bisuteria-relojes-complementos-mujer") : Mujer()
      data class CollaresMujer(override val urlSection: String = "collares") : Mujer()
      data class CinturonesMujer(override val urlSection: String = "cinturones-complementos-mujer") : Mujer()
      data class MochilasMujer(override val urlSection: String = "mochilas-complementos-mujer") : Mujer()
      data class RelojesMujer(override val urlSection: String = "relojes-mujer") : Mujer()
      data class PanuelosYBufandas(override val urlSection: String = "panuelos-mujer") : Mujer()
    }
  }

  /**
   * Url format: https://www.zalando.es/zapatillas-hombre/_plateado/
   */
  fun get(category: ZalandoCategory, color: ZalandoColor): List<ZalandoItem> {
    val doc = Jsoup.connect("https://www.zalando.es/${category.urlSection}/${color.name}/").get()
    println(doc.title())
    val itemsGridDiv = doc.body().select("z-grid.cat_catArticles-2Pxh7")
    return itemsGridDiv[0].children().fold(listOf()) { acc, gridItem ->

      val imageItems = gridItem.select("[class^=cat_image-]")
      if (imageItems.isNotEmpty()) {
        val imageUrl = gridItem.select("[class^=cat_image-]").first().attr("src")
        val url = "https://www.zalando.es${gridItem.select("[class^=cat_infoDetail]").first().attr("href")}"
        val name = gridItem.select("[class^=cat_brandName]").first().text()

        val originalPrice = gridItem.select("[class^=cat_originalPrice]")
        val promotionalPriceItems = gridItem.select("[class^=cat_promotionalPrice]")

        val price = if (promotionalPriceItems.isNotEmpty()) {
          promotionalPriceItems.first().text()
        } else {
          originalPrice.text()
        }

        acc + ZalandoItem(imageUrl, url, name, price)
      } else {
        acc
      }
    }
  }

  /**
   * Assumes the String is a HEX color with the format #FFFFFF or FFFFFF
   */
  private fun String.toRGB(): Color {
    val color = if (this.startsWith("#")) {
      Color.decode("0x${this.drop(1)}")
    } else {
      Color.decode("0x${this}")
    }
    val red = color.red
    val blue = color.blue
    val green = color.green
    return Color(red, green, blue)
  }

  private fun Color.colorSum(target: Color) =
    sqrt(abs(this.red.toFloat() - target.red)) +
      sqrt(abs(this.green.toFloat() - target.green)) +
      sqrt(abs(this.blue.toFloat() - target.blue))

  fun get(category: ZalandoCategory, source: String): List<ZalandoItem> {
    // (Square(Red(source)-Red(target))) +
    // (Square(Green(source)-Green(target))) +
    // (Square(Blue(source)-Blue(target)))
    val closestColor = ZalandoColor.all().reduce { acc, currentColor ->
      if (acc.hex.toRGB().colorSum(source.toRGB()) < currentColor.hex.toRGB().colorSum(source.toRGB())) {
        acc
      } else {
        currentColor
      }
    }
    return get(category, closestColor)
  }
}

fun main() {
  val apiClient = ZalandoApiClient()
  val res = apiClient.get(ZalandoCategory.Mujer.ChaquetasPuntoYJerseysMujer(), "#d9849b")
  println(res)
}
