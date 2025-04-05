package adapter.`in`

import adapter.`in`.Expiration.ONE_DAY
import adapter.`in`.Expiration.ONE_MONTH
import adapter.`in`.page.aboutPage
import adapter.`in`.page.mainPage
import adapter.`in`.page.postPage
import adapter.out.findAllPosts
import adapter.out.findPost
import domain.Language
import domain.PostId
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route

object Controller {
	
	private val redirectToLocalizedPage: suspend RoutingContext.() -> Unit = {
		call.expiresAfter(ONE_MONTH)
		val language = getLanguageFromHeader(call) ?: Language.Default
		call.respondRedirect("/${language.name.lowercase()}")
	}
	
	private val mainPage: suspend RoutingContext.() -> Unit = {
		call.expiresAfter(ONE_DAY)
		val language = getLanguageFromUrl(call)
		val posts = findAllPosts(language)
		call.respondHtml(block = mainPage(language, posts))
	}
	
	private val aboutPage: suspend RoutingContext.() -> Unit = {
		call.expiresAfter(ONE_MONTH)
		val language = getLanguageFromUrl(call)
		call.respondHtml(block = aboutPage(language))
	}
	
	private val postPage: suspend RoutingContext.() -> Unit = {
		call.parameters["postId"]
			?.let { findPost(PostId(it.toLong())) }
			?.let { post ->
				call.expiresAfter(ONE_MONTH)
				val language = getLanguageFromUrl(call)
				call.respondHtml(block = postPage(language, post))
			}
	}
	
	fun Routing.pages() {
		get("", redirectToLocalizedPage)
		
		route("{language}") {
			get("", mainPage)
			get("about", aboutPage)
			get("post/{postId}", postPage)
		}
	}
	
	private fun getLanguageFromHeader(call: ApplicationCall): Language? =
		call.request.headers["Accept-language"]?.let(::getLanguageFromHeader)
	
	private fun getLanguageFromUrl(call: ApplicationCall) = Language[call.parameters["language"]!!]
}
