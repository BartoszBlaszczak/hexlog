package adapter.`in`

import AppProperties
import adapter.`in`.AtomController.atom
import adapter.`in`.Controller.pages
import adapter.`in`.Expiration.ONE_MONTH
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.httpsredirect.HttpsRedirect
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.expires
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.routing
import org.slf4j.event.Level
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.time.LocalDateTime

object KtorServer {
	private fun server() = embeddedServer(Netty, applicationEngineEnvironment {
		if (AppProperties.useSSL) connectSSL()
		connector { port = AppProperties.httpPort }
		
		module {
			if (AppProperties.useSSL) install(HttpsRedirect) { sslPort = AppProperties.externalHttpsPort }
			
			install(StatusPages) {
				exception<NoSuchElementException> { call, exception -> exception.message; call.respond(NotFound) }
				
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
							"${it.request.httpMethod.value} ${it.request.local.scheme}://${it.request.local.serverHost}${it.request.uri} " +
							"FROM: ${it.request.headers["User-agent"]} ${it.request.headers["Accept-language"]} " +
							">> ${it.response.status()?.description} ${it.response.headers["location"] ?: ""}"
				}
			}
			
			routing {
				get("health") { call.respond(OK, "OK") }
				head("health") { call.respond(OK) }
				staticFiles(remotePath = "/posts", dir = File("posts")) { modify { _, call -> call.expiresAfter(ONE_MONTH) } }
				staticResources("/web/static", "web/static") { modify { _, call -> call.expiresAfter(ONE_MONTH) } }
				pages()
				atom()
			}
		}
	})
	
	private fun ApplicationEngineEnvironmentBuilder.connectSSL() {
		val keystorePassword = AppProperties.keystorePassword.toCharArray()
		sslConnector(
			keyStore = KeyStore.getInstance(AppProperties.keystoreType).apply {
				load(FileInputStream(AppProperties.keystorePath), keystorePassword)
			},
			keyAlias = AppProperties.certAlias,
			keyStorePassword = { keystorePassword },
			privateKeyPassword = { keystorePassword }
		) { port = AppProperties.httpsPort }
	}
	
	fun run() = server().start(wait = true)
}

fun ApplicationCall.expiresAfter(expiration: Expiration) = response.expires(expiration.date())

enum class Expiration(val date: () -> LocalDateTime) {
	ONE_MONTH({ LocalDateTime.now().plusMonths(1) }),
	ONE_DAY({ LocalDateTime.now().plusDays(1) });
}
