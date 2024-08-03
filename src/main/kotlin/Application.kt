
import adapter.`in`.KtorServer
import java.lang.Runtime.getRuntime

fun main() {
    configureLogs()
    KtorServer.run()
}

private fun configureLogs() {
    System.setProperty("java.util.logging.config.file", "log.properties")
}

fun <T: Any> T.atShutdown(hook: (T) -> Unit): T = this.also { getRuntime().addShutdownHook(Thread { hook(this) }) }
