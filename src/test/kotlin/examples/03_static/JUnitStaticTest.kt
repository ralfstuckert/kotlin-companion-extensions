package examples.statics

import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

// JUnit 4 requires @BeforeClass / @AfterClass to be *public static void*.
//
// With a companion object you have to remember @JvmStatic:
//
//   class Tests {
//       companion object {
//           @BeforeClass @JvmStatic
//           fun setup() { ... }
//       }
//   }
//
// Forget it, and JUnit silently never runs the hook - the kind of bug the
// KEEP calls out as problem #2.
//
// A companion block is static by construction, so the annotation is enough.
class JUnitStaticTest {

    companion {
        lateinit var database: String

        @BeforeClass
        fun setUpClass() {
            database = "in-memory-db"
            println("@BeforeClass ran - database = $database")
        }

        @AfterClass
        fun tearDownClass() {
            println("@AfterClass ran")
        }
    }

    @Test
    fun `class level fixture was initialized`() {
        assertEquals("in-memory-db", database)
    }
}
