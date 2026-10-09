package com.example.tableo.data.enums

/**
 * Sprachen, die die App kann. Der Name steht immer in der Sprache selbst,
 * damit man ihn auch findet, wenn die App gerade in einer fremden Sprache läuft.
 *
 * @param code ISO-Code für die Ressourcen-Ordner (values-en usw.), null = Sprache vom Handy
 */
enum class AppLanguage(val code: String?, val nativeName: String) {
    SYSTEM(null, "System"),
    DEUTSCH("de", "Deutsch"),
    ENGLISH("en", "English"),
    TUERKCE("tr", "Türkçe"),
    ARABIC("ar", "العربية"),
    ESPANOL("es", "Español"),
    FRANCAIS("fr", "Français"),
    ITALIANO("it", "Italiano"),
    POLSKI("pl", "Polski"),
    RUSSIAN("ru", "Русский"),
    UKRAINIAN("uk", "Українська");

    /** Arabisch liest sich von rechts nach links. */
    val isRightToLeft: Boolean get() = code == "ar"

    companion object {
        fun fromCode(code: String?): AppLanguage = entries.find { it.code == code } ?: SYSTEM
    }
}
