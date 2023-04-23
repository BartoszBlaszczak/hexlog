
object AppProperties {
	
	val useSSL: Boolean = getProperty("useSSL", "true").toBoolean()
	val httpPort: Int = getProperty("httpPort", "8060").toInt()
	val httpsPort: Int = getProperty("httpsPort", "8066").toInt()
	val externalHttpsPort: Int = getProperty("externalHttpsPort", "443").toInt()
	val keystoreType: String = getProperty("keystoreType", "JKS")
	val keystorePath: String = getProperty("keystorePath", "keystore.jks")
	val certAlias: String = getProperty("certAlias", "hexlog-cert")
	val keystorePassword: String = getProperty("keystorePassword", "")
	val dbURL: String = getProperty("dbURL", "jdbc:sqlite:database.sqlite")
	val address: String = getProperty("address", "https://hexlog.dev")
	
	init {
		logger.info("""HexLogProperties:
			|useSSL: $useSSL
			|httpPort: $httpPort
			|httpsPort: $httpsPort
			|externalHttpsPort: $externalHttpsPort
			|keystoreType: $keystoreType
			|keystorePath: $keystorePath
			|certAlias: $certAlias
			|dbURL: $dbURL
			|address: $address
			|""".trimMargin())
	}
}

fun getProperty(key: String, defaultValue: String): String =
	System.getProperty(key) ?: System.getenv(key) ?: defaultValue
