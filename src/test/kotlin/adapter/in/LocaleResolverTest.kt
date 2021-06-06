package adapter.`in`

import domain.Language.EN
import domain.Language.PL
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class LocaleResolverTest : FunSpec({
    
    test("evaluate language from header") {
        table(
            headers("header", "expected"),
            row(null, null),
            row("", null),
            row("en-us;q=0.7,en;q=0.3", EN),
            row("en-us;q=0.7,pl;q=0.9,en;q=0.3", PL),
            row("de,en-us;q=0.7,pl;q=0.6,en;q=0.3", EN),
            row("pl,en-us;q=0.7,en;q=0.3", PL),
            row("pl-PL,en-us;q=0.7,en;q=0.3", PL),
            row("de,pl-PL;q=0.9,en-us;q=0.7,en;q=0.3", PL),
        ).forAll { header, expected ->
            LocaleResolver.getLanguageFromHeader(header) shouldBe expected
        }
    }
})
