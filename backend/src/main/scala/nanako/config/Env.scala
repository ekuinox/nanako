package nanako.config

case class EnvConfig():
  private val allEnvVar: Map[String, String] = sys.env

  val devMode: Boolean =
    allEnvVar.getOrElse(
      "NANAKO_DEV_MODE",
      default = "true"
    ) == "true" // To handle different behaviors in dev and prod environments
  val port: Int = allEnvVar.getOrElse("NANAKO_PORT", default = "8080").toInt

  val postgresIp: String =
    allEnvVar.getOrElse("NANAKO_POSTGRES_IP", default = "localhost")
  val postgresPort: Int =
    allEnvVar.getOrElse("NANAKO_POSTGRES_PORT", default = "5432").toInt
  val postgresDb: String =
    allEnvVar.getOrElse("NANAKO_POSTGRES_DB", default = "nanako")
  val postgresUser: String =
    allEnvVar.getOrElse("NANAKO_POSTGRES_USER", default = "nanako")
  val postgresPassword: String =
    allEnvVar.getOrElse("NANAKO_POSTGRES_PASSWORD", default = "nanako")
  val postgresSchema: String =
    allEnvVar.getOrElse("NANAKO_POSTGRES_SCHEMA", default = "nanako")

object EnvConfig:
  implicit val impl: EnvConfig = EnvConfig()
