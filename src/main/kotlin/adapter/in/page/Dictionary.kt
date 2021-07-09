package adapter.`in`.page

import domain.Language
import domain.Language.EN
import domain.Language.PL

interface Dictionary {
	val feed: String
	val description: String
	val keywords: String
	val about: String
	val polish: String
	val english: String
}

val dictionaries: Map<Language, Dictionary> = mapOf(PL to Polish, EN to English)

private object Polish : Dictionary {
	override val feed = "Powiadomienia dla Hex.log"
	override val description = "Blog Programistyczny"
	override val keywords = "programowanie, rozwój oprogramowania, rzemiosło programowania, czysty kod, Kotlin, DDD"
	override val about = "O blogu"
	override val polish = "polski"
	override val english = "angielski"
}

private object English : Dictionary {
	override val feed = "Feed for Hex.log"
	override val description = "Software Development Blog"
	override val keywords = "Programming, Software Development, Software Craftsmanship, Clean Code, Kotlin, DDD"
	override val about = "About"
	override val polish = "Polish"
	override val english = "English"
}
