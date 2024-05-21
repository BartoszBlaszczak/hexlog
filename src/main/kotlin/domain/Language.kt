package domain

import java.util.Locale

enum class Language(val locale: Locale) {
    PL(Locale.of("pl")),
    EN(Locale.ENGLISH);

    fun supports(locale: Locale): Boolean = this.locale.language.equals(locale.language, ignoreCase = true)
    
    override fun toString(): String = name.lowercase()
    
    companion object {
        val Default = EN
        
        fun find(value: String): Language =
            values().find { value.uppercase() == it.name } ?: throw NoSuchElementException()
    }
}
