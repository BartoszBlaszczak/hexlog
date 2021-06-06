package adapter.`in`

import TestContext
import io.kotest.core.spec.style.FunSpec
import start

abstract class RunningAppTest(body: FunSpec.() -> Unit) : FunSpec(body) {
	companion object {
		init {
			System.setProperty("io.ktor.development", "false") //in development mode, Ktor replaces ClassLoader, so HexLogContext does not change (see ContextTest)
			start(TestContext)
		}
	}
}
