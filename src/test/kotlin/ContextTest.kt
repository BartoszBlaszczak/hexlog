import io.kotest.core.spec.style.FunSpec

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class ContextTest(body: FunSpec.() -> Unit) : FunSpec(body) {
	companion object { init { appContext = TestContext } }
}
