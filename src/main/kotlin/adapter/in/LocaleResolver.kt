package adapter.`in`

import domain.Language
import java.util.Locale

object LocaleResolver {
	fun getLanguageFromHeader(acceptLanguageHeaderValue: String?): Language? =
		acceptLanguageHeaderValue
			?.split(',')
			?.map(::AcceptLanguageHeaderSingleValue)
			?.filter(AcceptLanguageHeaderSingleValue::isSupported)
			?.maxOrNull()
			?.supportedLanguage
}

class AcceptLanguageHeaderSingleValue(headerValue: String) : Comparable<AcceptLanguageHeaderSingleValue> {
	private val locale: Locale
	private val q: Float
	val supportedLanguage: Language?
	
	init {
		val headerParts = headerValue.split(';')
		q = headerParts.elementAtOrNull(1)?.substringAfter('=')?.toFloat() ?: 1.toFloat()
		val localeParts = headerParts[0].split('-')
		locale = Locale.of(localeParts[0], localeParts.elementAtOrNull(1) ?: "", localeParts.elementAtOrNull(2) ?: "")
		supportedLanguage = Language.values().firstOrNull { it.supports(locale) }
	}
	
	fun isSupported() = supportedLanguage != null
	
	override fun compareTo(other: AcceptLanguageHeaderSingleValue): Int = q.compareTo(other.q)
}
