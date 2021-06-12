package adapter.`in`

import appContext
import com.rometools.rome.feed.synd.SyndCategoryImpl
import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.SyndFeedOutput
import domain.Language
import io.ktor.application.*
import io.ktor.http.ContentType.Application.Atom
import io.ktor.response.*
import io.ktor.routing.*
import java.time.ZoneId.systemDefault
import java.util.Date

object AtomController {
	private val postsRepository by appContext.postsRepository
	private val outputGenerator = SyndFeedOutput()
	
	
	fun Routing.atom() {
		get("{language}/feed") {
			val language = Language.find(call.parameters["language"]!!)
			call.respondText(contentType = Atom) { prepareFeed(language) }
		}
	}
	
	private fun prepareFeed(language: Language): String {
		val feed: SyndFeed = SyndFeedImpl()
		feed.feedType = "atom_1.0"
		feed.title = "< Hex.log >"
		feed.description = "programming blog"
		feed.link = "https://hexlog.dev"
		feed.language = language.locale.toString()
		
		feed.entries = postsRepository.findAll(language).map { post ->
			SyndEntryImpl().apply {
				title = post.title
				publishedDate = Date.from(post.createDate.atZone(systemDefault()).toInstant())
				author = "Hex"
				link = "/$language/post/${post.id?.value}"
				categories = listOf(SyndCategoryImpl().apply { name = "Software development" })
				description = SyndContentImpl().apply { value = post.shortcut }
			}
		}
		
		return outputGenerator.outputString(feed)
	}
}
