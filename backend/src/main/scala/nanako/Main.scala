package nanako

import cats.effect.*
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.client3.*
import sttp.shared.Identity
import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import scala.concurrent.ExecutionContext
import nanako.config.DbConfig
import nanako.config.ApiConfig

object HelloWorldHttp4sServer extends IOApp:
  // the endpoint: single fixed path input ("hello"), single query parameter
  // corresponds to: GET /hello?name=...
  val helloWorld: PublicEndpoint[String, Unit, String, Any] =
    endpoint.get.in("hello").in(query[String]("name")).out(stringBody)

  // converting an endpoint to a route (providing server-side logic); extension method comes from imported packages
  val helloWorldRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(
      helloWorld.serverLogic(name => IO(s"Hello, $name!".asRight[Unit]))
    )

  override def run(args: List[String]): IO[ExitCode] =
    DbConfig.impl.setup >> ApiConfig.impl.setup >> IO.never
