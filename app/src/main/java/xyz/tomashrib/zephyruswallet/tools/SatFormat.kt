package xyz.tomashrib.zephyruswallet.tools

fun formatSats(balanceInSats: String): String{
    val reversed = balanceInSats.reversed()
    val withCommas = reversed.chunked(3).joinToString(",")

    return withCommas.reversed()
}