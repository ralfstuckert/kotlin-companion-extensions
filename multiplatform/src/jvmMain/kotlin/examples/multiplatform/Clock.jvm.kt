package examples.multiplatform

// Note the modifier placement: the companion *block* carries no `actual` -
// it is not a declaration that can be matched. Only its members are actual.
//
//   ./gradlew javapMultiplatform
//
// shows `public static final Clock system()` on the class, with no
// Clock$Companion anywhere.
actual class Clock(actual val epochMillis: Long) {
    companion {
        actual fun system(): Clock = Clock(System.currentTimeMillis())
    }
}
