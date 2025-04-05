
import adapter.`in`.KtorServer
import adapter.out.prepareDB
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Runtime.getRuntime

fun main() {
    prepareDB()
    KtorServer.run()
}

val logger: Logger = run {
    System.setProperty("java.util.logging.config.file", "log.properties")
    LoggerFactory.getLogger("")
}

fun <T: Any> T.atShutdown(hook: (T) -> Unit): T = this.also { getRuntime().addShutdownHook(Thread { hook(this) }) }
