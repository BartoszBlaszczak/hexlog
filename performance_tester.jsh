import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.net.http.HttpClient.Redirect.ALWAYS;
import static java.net.http.HttpResponse.BodyHandlers.ofString;

	try {
		HttpClient client = HttpClient.newBuilder().followRedirects(ALWAYS).build();
		HttpRequest requestMain = HttpRequest.newBuilder(new URI("https://hexlog.dev")).build();
		HttpRequest requestPage = HttpRequest.newBuilder(new URI("https://hexlog.dev/posts/1.html")).build();
		int attempts = 10;

		LocalDateTime startTime = LocalDateTime.now();

		for (int i = 1; i < attempts; i++) {
			client.send(requestMain, ofString());
			client.send(requestPage, ofString());
		}

		Duration durationOfTest = Duration.between(startTime, LocalDateTime.now());

		System.out.println("$attempts requests in" + durationOfTest + " seconds");
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
/exit