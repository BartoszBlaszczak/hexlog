package adapter.`in`.page

import domain.Language
import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.lang
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun page(language: Language, mainBody: DIV.() -> Unit): HTML.() -> Unit = {
	lang = language.toString()
	this.head(head(language))
	body {
		onResize = "resize_posts()"
		header("main-header", header(language))
		div("main-body", mainBody)
		br()
	}
}
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
fun LocalDateTime.formatted(): String = format(dateFormatter)
