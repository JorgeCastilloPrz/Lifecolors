package dev.jorgecastillo.lifecolors.clothes.network.serializers

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import dev.jorgecastillo.zalandoclient.ZalandoApiClient.ZalandoCategory

class ZalandoCategorySerializer {

    @ToJson
    fun toJson(category: ZalandoCategory): String {
        return category.urlSection
    }

    @FromJson
    fun fromJson(category: String): ZalandoCategory {
        return when (category) {
            "ropa-de-mujer" -> ZalandoCategory.Mujer.RopaMujer()
            "calzado-de-mujer" -> ZalandoCategory.Mujer.CalzadoMujer()
            "ropa-de-mujer-vestidos" -> ZalandoCategory.Mujer.VestidosMujer()
            "ropa-de-mujer-camisetas-y-tops" -> ZalandoCategory.Mujer.CamisetasYTopsMujer()
            "ropa-de-mujer-blusas-y-blusones" -> ZalandoCategory.Mujer.CamisasYBlusasMujer()
            "pantalones-mujer" -> ZalandoCategory.Mujer.PantalonesMujer()
            "pantalones-vaqueros-mujer" -> ZalandoCategory.Mujer.VaquerosMujer()
            "pantalones-monos-mujer" -> ZalandoCategory.Mujer.MonosMujer()
            "faldas-mujer" -> ZalandoCategory.Mujer.FaldasMujer()
            "chaquetas-de-punto-y-jerseis-mujer" -> ZalandoCategory.Mujer.ChaquetasPuntoYJerseysMujer()
            "sandalias-mujer" -> ZalandoCategory.Mujer.SandaliasMujer()
            "zapatillas-mujer" -> ZalandoCategory.Mujer.ZapatillasMujer()
            "alpargatas-mujer" -> ZalandoCategory.Mujer.AlpargatasMujer()
            "zapatos-bajos-mujer" -> ZalandoCategory.Mujer.ZapatosPlanosMujer()
            "zuecos-mujer" -> ZalandoCategory.Mujer.ZuecosMujer()
            "bailarinas-mujer" -> ZalandoCategory.Mujer.BailarinasMujer()
            "zapatos-bajos-mocasines-mujer" -> ZalandoCategory.Mujer.MocasinesMujer()
            "botines-mujer" -> ZalandoCategory.Mujer.BotinesMujer()
            "botas-mujer" -> ZalandoCategory.Mujer.BotasMujer()
            "zapatillas-deporte-mujer" -> ZalandoCategory.Mujer.ZapatillasDeporteMujer()
            "zapatos-altos-mujer" -> ZalandoCategory.Mujer.ZapatosTaconMujer()
            "gafas-sol-complementos-mujer" -> ZalandoCategory.Mujer.GafasDeSolMujer()
            "bolsos-maletas-complementos-mujer" -> ZalandoCategory.Mujer.BolsosMujer()
            "monederos-complementos-mujer" -> ZalandoCategory.Mujer.MonederosMujer()
            "gorros-sombreros-gorras-complementos-mujer" -> ZalandoCategory.Mujer.GorrosYSombrerosMujer()
            "bisuteria-relojes-complementos-mujer" -> ZalandoCategory.Mujer.BisuteriaMujer()
            "collares" -> ZalandoCategory.Mujer.CollaresMujer()
            "cinturones-complementos-mujer" -> ZalandoCategory.Mujer.CinturonesMujer()
            "mochilas-complementos-mujer" -> ZalandoCategory.Mujer.MochilasMujer()
            "relojes-mujer" -> ZalandoCategory.Mujer.RelojesMujer()
            "panuelos-mujer" -> ZalandoCategory.Mujer.PanuelosYBufandas()
            else -> ZalandoCategory.Mujer.RopaMujer()
        }
    }
}
