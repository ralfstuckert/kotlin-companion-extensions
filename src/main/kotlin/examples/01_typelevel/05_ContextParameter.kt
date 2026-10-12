package examples.typelevel

import examples.motivation.Color

interface Theme {
    val primary: Int
}

object LightTheme : Theme {
    override val primary = 0x6750A4
}

object DarkTheme : Theme {
    override val primary = 0xD0BCFF
}

// Being top level, this could only ever be one fixed colour:
//   companion val Color.BrandPrimary: Color get() = Color(0x6750A4)
// Unless we ask for the scope it needs.
context(theme: Theme)
companion val Color.BrandPrimary: Color get() = Color(theme.primary)

fun main() {
    context(LightTheme) { println(Color.BrandPrimary) }
    context(DarkTheme) { println(Color.BrandPrimary) }

    // Color.BrandPrimary  -> No context argument for 'theme: Theme' found.

    println(Color.Black)
}
