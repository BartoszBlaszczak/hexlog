package adapter.`in`.page

import domain.Language
import domain.Post
import kotlinx.html.HTML
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.iframe
import kotlinx.html.onClick
import kotlinx.html.onLoad
import kotlinx.html.p
import kotlinx.html.span

fun mainPage(language: Language, posts: Array<Post>): HTML.() -> Unit = page(language) {
	br()
	posts.forEachIndexed { index, post ->
		div("post") {
			button(classes = "post-header openable") {
				id = "${post.id?.value}"
				onClick = "toggle_collapse(this)"
				attributes["onAuxClick"] = "window.open('/${language}/post/${post.id?.value}')"
				p {
					span("post-date") { +post.createDate.formatted() }
					span("post-title") { +post.title }
				}
				p { +post.shortcut }
			}
			div("post-panel") {
				iframe(sandbox = null, "post-frame") {
					id = "post_${post.id?.value}_iframe"
					src = "posts/${post.id?.value}.html"
					onLoad = "resize_post(this)"
					attributes["loading"] = "lazy"
				}
			}
			if (index != posts.lastIndex) hr("post-separator")
		}
	}
}
