package adapter.`in`.page

import domain.Language
import domain.Post
import kotlinx.html.HTML
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.iframe
import kotlinx.html.onLoad
import kotlinx.html.p
import kotlinx.html.span

fun postPage(language: Language, post: Post): HTML.() -> Unit = page(language) {
	br()
	div("post") {
		button(classes = "post-header active") {
			p {
				span("post-date") { +post.createDate.formatted() }
				span("post-title") { +post.title }
			}
			p { +post.shortcut }
		}
		div("post-panel") {
			iframe(classes = "post-frame") {
				onLoad = "resize_post(this)"
				src = "/posts/${post.id.value}.html"
			}
		}
	}
}
