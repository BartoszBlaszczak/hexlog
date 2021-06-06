
import adapter.`in`.KtorServer

lateinit var appContext : AppContext

fun main(args: Array<String>) = start(AppContext(args))

fun start(ctx: AppContext) {
    appContext = ctx
    configureLogs()
    KtorServer.run()
}

private fun configureLogs() {
    System.setProperty("java.util.logging.config.file", "log.properties")
}
