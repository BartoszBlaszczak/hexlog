package adapter.`in`

import adapter.`in`.AtomController.atom
import adapter.`in`.Controller.pages
import adapter.`in`.Expiration.ONE_MONTH
import appContext
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline.ApplicationPhase.Call
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.HttpsRedirect
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.content.files
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.response.expires
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.head
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.event.Level
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
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
			
			install(Thymeleaf) {
				setTemplateResolver(ClassLoaderTemplateResolver().apply {
					prefix = "/web/template/"
					characterEncoding = "utf-8"
					addDialect(Java8TimeDialect())
				})
			}
			
			install(StatusPages) {
				exception<NoSuchElementException> { it.message; call.respond(NotFound) }
				
				exception<Throwable> { cause ->
					cause.localizedMessage?.let(log::error)
					log.error(cause.stackTraceToString())
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
