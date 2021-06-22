package adapter.`in`

import adapter.`in`.Expiration.ONE_DAY
import adapter.`in`.Expiration.ONE_MONTH
import adapter.`in`.LocaleResolver.getLanguageFromHeader
import appContext
import domain.Language
import domain.PostId
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.thymeleaf.ThymeleafContent
import io.ktor.util.pipeline.PipelineInterceptor
import java.util.Locale

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
		call.respond(content(language, "index.html", mapOf("posts" to repository.findAll(language))))
	}
	
	private val aboutPage: PipelineInterceptor<Unit, ApplicationCall> = {
		setExpiration(ONE_MONTH)
		val language = getLanguageFromUrl(call)
		call.respond(content(language, "about.html"))
	}
	
	private val postPage: PipelineInterceptor<Unit, ApplicationCall> = {
		call.parameters["postId"]
			?.let { repository.find(PostId(it.toLong())) }
			?.let { post ->
				setExpiration(ONE_MONTH)
				val language = getLanguageFromUrl(call)
				call.respond(content(language, "post.html", mapOf("post" to post)))
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
	
	private fun getLanguageFromUrl(call: ApplicationCall) = Language.find(call.parameters["language"]!!)
	
	private fun getLanguageFromHeader(call: ApplicationCall): Language? =
		call.request.headers["Accept-language"]?.let(::getLanguageFromHeader)
	
	private fun content(language: Language, template: String, model: Map<String, Any> = mapOf()) = synchronized(this) {
		Locale.setDefault(language.locale) //see https://github.com/ktorio/ktor/pull/1951
		return@synchronized ThymeleafContent(template, model)
	}
}
