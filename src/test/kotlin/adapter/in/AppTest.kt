package adapter.`in`

import PostCreator
import appContext
import domain.Language
import domain.Post
import domain.PostId
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import io.ktor.http.withCharset
import java.lang.ClassLoader.getSystemResource
import java.nio.charset.Charset.defaultCharset
import java.time.LocalDate
import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME

class AppTest : RunningAppTest({
	this as AppTest
	
	beforeSpec { insertFewPosts() }
	
	test("health check") {
		// when
		val response: HttpResponse = client.get("$address/health")
		// then
		response.status shouldBe OK
	}
	
	test("redirect to localized page") {
		// given
		table(
			headers("client language", "expected redirection"),
			row("pl", "/pl"),
			row("en", "/en"),
			row("es", "/en"),
			row(null, "/en"),
		).forAll { clientLanguage, expectedRedirection ->
			// when
			val response: HttpResponse = client.get(address) { clientLanguage?.let { header("Accept-language", it) } }
			
			// then
			response.status shouldBe Found
			response.headers["Location"] shouldBe expectedRedirection
			getExpirencyDate(response) shouldBe nextMonth
		}
	}
	
	test("get page") {
		// given
		table(
			headers("path", "expirency date", "expected content"),
			row("/en/about", nextMonth, "expected_about_page_en.html"),
			row("/pl/about", nextMonth, "expected_about_page_pl.html"),
			row("/en", nextDay, "expected_main_page_en.html"),
			row("/pl", nextDay, "expected_main_page_pl.html"),
			row("/en/post/2", nextMonth, "expected_post_page_en.html"),
			row("/pl/post/1", nextMonth, "expected_post_page_pl.html"),
		).forAll { path, expirency_date, expected_content_path ->
			// when
			val response: HttpResponse = client.get(address + path)
			
			// then
			response.status shouldBe OK
			getExpirencyDate(response) shouldBe expirency_date
			response.readText() shouldBe getSystemResource(expected_content_path).readText()
			
			response.headers["X-Frame-Options"] shouldBe "SAMEORIGIN"
			response.headers["X-XSS-Protection"] shouldBe "1"
			response.headers["X-Content-Type-Options"] shouldBe "nosniff"
			response.headers["Strict-Transport-Security"] shouldBe "max-age=31536000"
			response.headers["Content-Security-Policy"] shouldBe "default-src 'self' data: 'unsafe-inline'"
		}
	}
	
	test("get 404") {
		table(
			headers("path"),
			row("/xx/about"),
			row("/spoon/about"),
			row("/pl/spoon"),
			row("/spoon"),
		).forAll { path ->
			// when
			val response: HttpResponse = client.get(address + path)
			
			// then
			response.status shouldBe NotFound
			response.readText() shouldBe ""
		}
	}
	
	test("get feed") {
		table(
			headers("path", "expected content"),
			row("/en/feed", "expected_feed_en.xml"),
			row("/pl/feed", "expected_feed_pl.xml"),
		).forAll { path, expectedContent ->
			// when
			val response: HttpResponse = client.get(address + path)
			
			// then
			response.status shouldBe OK
			response.contentType() shouldBe ContentType.Application.Atom.withCharset(defaultCharset())
			response.readText() shouldBe getSystemResource(expectedContent).readText()
		}
	}
}) {
	private val appProperties by appContext.properties
	private val client = HttpClient(Java) { followRedirects = false; expectSuccess = false }
	private val address = "http://localhost:${appProperties.httpPort}"
	
	private val nextDay = LocalDate.now().plusDays(1)
	private val nextMonth = LocalDate.now().plusMonths(1)
	
	private fun getExpirencyDate(response: HttpResponse) = LocalDate.parse(response.headers["Expires"]!!, RFC_1123_DATE_TIME)
	
	fun insertFewPosts() {
		val createDate = LocalDate.parse("2021-05-05").atStartOfDay()
		PostCreator.insert(Post(id = PostId(1), createDate = createDate, title = "tytuł", shortcut = "streszczenie", language = Language.PL))
		PostCreator.insert(Post(id = PostId(2), createDate = createDate, title = "title", shortcut = "shortcut", language = Language.EN))
	}
}