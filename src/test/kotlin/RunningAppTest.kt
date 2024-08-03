
import io.kotest.core.spec.style.FunSpec
import kotlin.concurrent.thread

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class RunningAppTest(body: FunSpec.() -> Unit) : FunSpec(body) {
	companion object {
		init {
			System.setProperty("useSSL", false.toString())
			System.setProperty("address", "http://localhost:8060")
			System.setProperty("dbURL", "jdbc:sqlite:file::memory:?cache=shared")
			thread { main() }
		}
	}
}
