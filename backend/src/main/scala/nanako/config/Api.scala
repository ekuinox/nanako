package nanako.config

import cats.effect.IO
import cats.implicits._
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.Port
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{Logger => LoggerMiddleware}
import org.http4s.server.middleware.CORS
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.syntax.LoggerInterpolator
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import org.http4s.HttpRoutes
import sttp.client3.*
import sttp.shared.Identity
import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.http4s.server.Router

case class ApiConfig()(implicit
    envConf: EnvConfig,
    logger: Logger[IO] = Slf4jLogger.getLogger
) {

  val helloWorld: PublicEndpoint[String, Unit, String, Any] =
    endpoint.get.in("hello").in(query[String]("name")).out(stringBody)

  // converting an endpoint to a route (providing server-side logic); extension method comes from imported packages
  val helloWorldRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(
      helloWorld.serverLogic(name => IO(s"Hello, $name!".asRight[Unit]))
    )

  def setup: IO[Unit] = for {
    port <-
      IO.fromOption(Port.fromInt(envConf.port))(
        new RuntimeException(s"Not processable port number ${envConf.port}.")
      )
    corsPolicy = CORS.policy.withAllowOriginHostCi(_ =>
      envConf.devMode
    ) // Essential for local development setup with an SPA running on a separate port
    _ <- EmberServerBuilder
      .default[IO]
      .withHost(
        ipv4"0.0.0.0"
      ) // Accept connections from any available network interface
      .withPort(port) // On a given port
      .withHttpApp(
        corsPolicy(allRts).orNotFound
      ) // Link all routes to the backend server
      .withHttpApp(Router("/" -> helloWorldRoutes).orNotFound)
      .build
      .use(_ => IO.never)
      .start
      .void
  } yield ()

  private val docsEpt =
    SwaggerInterpreter()
      .fromEndpoints[IO](List.empty, "Backend – TARP Stack ⛺", "1.0")
  private val allRts = {
    val loggerMiddleware =
      LoggerMiddleware
        .httpRoutes( // To log incoming requests or outgoing responses from the server
          logHeaders = true,
          logBody = true,
          redactHeadersWhen =
            _ =>
              !envConf.devMode, // Display header values exclusively during development mode
          logAction = Some((msg: String) => info"$msg")
        )(_)
    loggerMiddleware(Http4sServerInterpreter[IO]().toRoutes(docsEpt))
  }
}

object ApiConfig { implicit val impl: ApiConfig = ApiConfig() }
