package xyz.tomashrib.zephyruswallet.tools

// formats balance of satoshis
// inserts ',' every 1000s
// 100000 -> 100,000
// its better readable this way
fun formatSats(balanceInSats: String): String{
    val reversed = balanceInSats.reversed()
    val withCommas = reversed.chunked(3).joinToString(",")

    return withCommas.reversed()
}