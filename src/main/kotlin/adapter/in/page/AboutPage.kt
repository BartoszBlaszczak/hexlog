package adapter.`in`.page

import domain.Language
import kotlinx.html.HTML
import kotlinx.html.iframe
import kotlinx.html.onLoad

fun aboutPage(language: Language): HTML.() -> Unit = page(language) {
	iframe(classes = "post-frame") {
		onLoad = "resize_post(this)"
		src = "/posts/about_${language}.html"
	}
}
