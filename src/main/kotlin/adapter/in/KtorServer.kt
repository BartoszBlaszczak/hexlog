package adapter.`in`

import adapter.`in`.AtomController.atom
import adapter.`in`.Controller.pages
import adapter.`in`.Expiration.ONE_MONTH
import appContext
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Call
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.http.content.files
import io.ktor.server.http.content.resource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.httpsredirect.HttpsRedirect
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.expires
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.event.Level
import java.io.FileInputStream
import java.security.KeyStore
import java.time.LocalDateTime

object KtorServer {
	private val properties by appContext.properties
	
	private fun server() = embeddedServer(Netty, applicationEngineEnvironment {
		if (properties.useSSL) connectSSL()
		connector { port = properties.httpPort }
		
		module {
			if (properties.useSSL) install(HttpsRedirect) { sslPort = properties.externalHttpsPort }
			
			install(StatusPages) {
				exception<NoSuchElementException> { call, exception ->  exception.message; call.respond(NotFound) }
				
				exception<Throwable> { call, exception ->
					exception.localizedMessage?.let(log::error)
					log.error(exception.stackTraceToString())
					call.respond(InternalServerError)
				}
			}
			
			install(DefaultHeaders) {
				header("X-Frame-Options", "SAMEORIGIN")
				header("X-XSS-Protection", "1")
				header("X-Content-Type-Options", "nosniff")
				header("Strict-Transport-Security", "max-age=31536000")
				header("Content-Security-Policy", "default-src 'self' data: 'unsafe-inline'")
			}
			
			install(CallLogging) {
				level = Level.INFO
				filter { !it.request.uri.contains(Regex("static|posts/pics|favicon|health")) }
				format {
					"${it.request.headers["Referer"] ?: "direct"} REQUESTS " +
					"${it.request.httpMethod.value} ${it.request.local.scheme}://${it.request.local.host}${it.request.uri} " +
					"FROM: ${it.request.headers["User-agent"]} ${it.request.headers["Accept-language"]} " +
					">> ${it.response.status()?.description} ${it.response.headers["location"] ?: ""}"
				}
			}
			
			routing {
				get("health") { call.respond(OK, "OK") }
				head("health") { call.respond(OK) }
				static("/posts") { interceptExpiration(ONE_MONTH); files("posts") }
				static("/web/static") { interceptExpiration(ONE_MONTH); resources("web/static") }
				static { resource("/favicon.ico", "web/static/icons/favicon.ico") }
				pages()
				atom()
			}
		}
	})
	
	private fun ApplicationEngineEnvironmentBuilder.connectSSL() {
		val keystorePassword = properties.keystorePassword!!.toCharArray()
		sslConnector(
			keyStore = KeyStore.getInstance(properties.keystoreType).apply {
				load(FileInputStream(properties.keystorePath), keystorePassword)
			},
			keyAlias = properties.certAlias,
			keyStorePassword = { keystorePassword },
			privateKeyPassword = { keystorePassword }
		) { port = properties.httpsPort }
	}
	
	fun run() = server().start()
}

fun PipelineContext<*, ApplicationCall>.setExpiration(expiration: Expiration) {
	call.response.expires(expiration.date())
}

private fun Route.interceptExpiration(expiration: Expiration) {
	intercept(Call) { setExpiration(expiration) }
}

enum class Expiration(val date: () -> LocalDateTime) {
	ONE_MONTH({ LocalDateTime.now().plusMonths(1) }),
	ONE_DAY({ LocalDateTime.now().plusDays(1) });
}
