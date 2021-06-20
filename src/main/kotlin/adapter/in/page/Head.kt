package adapter.`in`.page

import domain.Language
import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.styleLink
import kotlinx.html.title

fun head(language: Language): HEAD.() -> Unit = {
	val dictionary = dictionaries[language]!!
	
	title("<Hex.log >")
	
	meta("description", dictionary.description, Charsets.UTF_8.name())
	meta("keywords", dictionary.keywords)
	meta("author", "Bartosz Błaszczak")
	meta("viewport", "initial-scale=0.7")
	
	styleLink("/web/static/style.css")
	styleLink("/web/static/fonts.css")
	script("text/javascript", "/web/static/hexlog_v2.js") {}
	
	link(href = "/$language/feed", rel = "alternate", type = "application/atom+xml") { title = dictionary.feed }
}
