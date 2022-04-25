package adapter.`in`

import adapter.`in`.Expiration.ONE_DAY
import adapter.`in`.Expiration.ONE_MONTH
import adapter.`in`.LocaleResolver.getLanguageFromHeader
import adapter.`in`.page.aboutPage
import adapter.`in`.page.mainPage
import adapter.`in`.page.postPage
import appContext
import domain.Language
import domain.PostId
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineInterceptor

object Controller {
	private val repository by appContext.postsRepository
	
	private val redirectToLocalizedPage: PipelineInterceptor<Unit, ApplicationCall> = {
		setExpiration(ONE_MONTH)
		val language = getLanguageFromHeader(call) ?: Language.Default
		call.respondRedirect("/${language.name.lowercase()}")
	}
	
	private val mainPage: PipelineInterceptor<Unit, ApplicationCall> = {
		setExpiration(ONE_DAY)
		val language = getLanguageFromUrl(call)
		val posts = repository.findAll(language)
		call.respondHtml(block = mainPage(language, posts))
	}
	
	private val aboutPage: PipelineInterceptor<Unit, ApplicationCall> = {
		setExpiration(ONE_MONTH)
		val language = getLanguageFromUrl(call)
		call.respondHtml(block = aboutPage(language))
	}
	
	private val postPage: PipelineInterceptor<Unit, ApplicationCall> = {
		call.parameters["postId"]
			?.let { repository.find(PostId(it.toLong())) }
			?.let { post ->
				setExpiration(ONE_MONTH)
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
	
	private fun getLanguageFromUrl(call: ApplicationCall) = Language.find(call.parameters["language"]!!)
}
