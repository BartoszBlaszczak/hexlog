package adapter.`in`.page

import domain.Language
import domain.Language.EN
import domain.Language.PL
import kotlinx.html.HEADER
import kotlinx.html.a
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.span
import kotlinx.html.title

fun header(language: Language): HEADER.() -> Unit = {
	val dictionary = dictionaries[language]!!
	a("/$language/about", classes = "main-header-link") { id = "about"; +dictionary.about }
	div {
		id = "languages"
		a("/pl", classes = "main-header-link") {
			title = dictionary.polish
			span("language-icon" + if (language == PL) " active" else "") { +"🇵🇱" }
		}
		+"\t\t"
		a("/en", classes = "main-header-link") {
			title = dictionary.english
			span("language-icon" + if (language == EN) " active" else "") { +"🏴󠁧󠁢󠁥󠁮󠁧󠁿" }
		}
	}
	h1 {
		br()
		a("/$language", classes = "main-header-link") {
			span("main-header-title") { +"< Hex.log >" }
			br()
			span { +"Code smart" }; span("red") { +"!" }
		}
	}
	br()
	h2 {
		a("https://github.com/BartoszBlaszczak/hexlog", "_blank", "main-header-link") {
			title = "GitHub"
			img("GitHub", "/web/static/icons/github.png", "main-header-icon")
		}
		+"\t\t"
		a("https://www.linkedin.com/in/bartosz-blaszczak", "_blank", "main-header-link") {
			title = "LinkedIn"
			img("LinkedIn", "/web/static/icons/linkedin.png", "main-header-icon")
		}
		+"\t\t"
		a("https://nofluffjobs.com/pl/profile/BartoszBlaszczak", "_blank", "main-header-link") {
			title = "No Fluff Jobs"
			img("No Fluff Jobs", "/web/static/icons/nfj.png", "main-header-icon")
		}
		+"\t\t"
		a("https://stackoverflow.com/users/13603661/hex", "_blank", "main-header-link") {
			title = "Stack Overflow"
			img("Stack Overflow", "/web/static/icons/stackoverflow.png", "main-header-icon")
		}
		+"\t\t"
		a("/$language/feed", "_blank", "main-header-link") {
			title = "Atom"
			img("Atom", "/web/static/icons/atom.png", "main-header-icon")
		}
		+"\t\t"
		a("mailto:bartosz.piotr.blaszczak@gmail.com", "_blank", "main-header-link") {
			title = "Email"
			img("Email", "/web/static/icons/email.png", "main-header-icon")
		}
		br()
		br()
	}
}
