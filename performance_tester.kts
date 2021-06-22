import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect.ALWAYS
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.LocalDateTime

val client: HttpClient = HttpClient.newBuilder().followRedirects(ALWAYS).build()
val requestMainPage = HttpRequest.newBuilder(URI("https://hexlog.dev")).build()
val requestPost = HttpRequest.newBuilder(URI("https://hexlog.dev/posts/1.html")).build()

val startTime = LocalDateTime.now()

val attempts = 1000
for (i in 1..attempts) {
	client.send(requestMainPage, HttpResponse.BodyHandlers.ofString())
	client.send(requestPost, HttpResponse.BodyHandlers.ofString())
}

val durationOfTest = Duration.between(startTime, LocalDateTime.now())

println("$attempts requests in $durationOfTest seconds")
