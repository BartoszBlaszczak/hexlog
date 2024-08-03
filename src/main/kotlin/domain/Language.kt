package domain

import java.util.Locale

enum class Language(val locale: Locale) {
    PL(Locale("pl")),
    EN(Locale.ENGLISH);

    fun supports(locale: Locale): Boolean = this.locale.language.equals(locale.language, ignoreCase = true)
    
    override fun toString(): String = name.lowercase()
    
    companion object {
        val Default = EN
        operator fun get(value: String): Language = entries.first { value.uppercase() == it.name }
    }
}
