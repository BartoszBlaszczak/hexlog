package adapter.`in`

import AppProperties
import adapter.out.findAllPosts
import com.rometools.rome.feed.synd.SyndCategoryImpl
import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.SyndFeedOutput
import domain.Language
import io.ktor.http.ContentType.Application.Atom
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import java.time.ZoneId.systemDefault
import java.util.Date

object AtomController {
	private val outputGenerator = SyndFeedOutput()
	
	fun Routing.atom() {
		get("{language}/feed") {
			val language = Language[call.parameters["language"]!!]
			call.respondText(contentType = Atom) { prepareFeed(language) }
		}
	}
	
	private fun prepareFeed(language: Language): String {
		val feed: SyndFeed = SyndFeedImpl()
		feed.feedType = "atom_1.0"
		feed.title = "< Hex.log >"
		feed.description = "programming blog"
		feed.link = "${AppProperties.address}/$language" //some readers don't work well with relative addresses
		feed.language = language.locale.toString()
		
		feed.entries = findAllPosts(language).map { post ->
			SyndEntryImpl().apply {
				title = post.title
				publishedDate = Date.from(post.createDate.atZone(systemDefault()).toInstant())
				author = "Hex"
				link = "${AppProperties.address}/$language/post/${post.id.value}"
				categories = listOf(SyndCategoryImpl().apply { name = "Software development" })
				description = SyndContentImpl().apply { value = post.shortcut }
			}
		}
		
		return outputGenerator.outputString(feed)
	}
}
