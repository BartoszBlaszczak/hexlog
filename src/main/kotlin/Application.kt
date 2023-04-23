
import adapter.`in`.KtorServer

fun main() {
    configureLogs()
    KtorServer.run()
}

private fun configureLogs() {
    System.setProperty("java.util.logging.config.file", "log.properties")
}
