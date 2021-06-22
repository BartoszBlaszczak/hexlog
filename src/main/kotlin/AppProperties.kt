import hex.PropertiesLoader
import org.slf4j.LoggerFactory
import java.util.Properties

class AppProperties(properties: Properties) {
	constructor(args: Array<String>) : this(PropertiesLoader.loadPropertiesFromDefaultSourcesAnd(args))
	private val logger = LoggerFactory.getLogger(javaClass)
	
	val useSSL: Boolean = properties.getProperty("useSSL", "true").toBoolean()
	val httpPort: Int = properties.getProperty("httpPort", "8060").toInt()
	val httpsPort: Int = properties.getProperty("httpsPort", "8066").toInt()
	val externalHttpsPort: Int = properties.getProperty("externalHttpsPort", "443").toInt()
	val keystoreType: String = properties.getProperty("keystoreType", "JKS")
	val keystorePath: String = properties.getProperty("keystorePath", "keystore.jks")
	val certAlias: String = properties.getProperty("certAlias", "hexlog-cert")
	val keystorePassword: String? = properties.getProperty("keystorePassword")
	
	val profiles: List<String> = properties.getProperty("hexlog_profiles", "").split(',')
	val dbURL: String = properties.getProperty("dbURL", "jdbc:sqlite:database.sqlite")
	val address: String = properties.getProperty("address")
	
	init {
		logger.info("""HexLogProperties:
			|useSSL: $useSSL
			|httpPort: $httpPort
			|httpsPort: $httpsPort
			|externalHttpsPort: $externalHttpsPort
			|keystoreType: $keystoreType
			|keystorePath: $keystorePath
			|certAlias: $certAlias
			|profiles: $profiles
			|dbURL: $dbURL
			|address: $address
			|""".trimMargin())
	}
}
