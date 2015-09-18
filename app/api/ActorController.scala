package api

import javax.inject.Inject

import akka.actor.{ActorSystem, Actor, Props}
import akka.util.Timeout
import api.HelloActor.SayHello
import play.api.mvc.{Action, Controller}

import akka.pattern.ask
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

//@Singleton
class ActorController @Inject() (system: ActorSystem)  extends Controller {
  val helloActor = system.actorOf(HelloActor.props, "hello-actor")

  implicit val timeout: Timeout = 5.seconds

  def sayHello(name: String) = Action.async {
    (helloActor ? SayHello(name)).mapTo[String].map { Ok(_) }
  }
}


/* This actor follows a few Akka conventions
*
* - The messages it sends/receives, or its protocol, are defined on its companion object
* - It also defines a props method on its companion object that returns the props for creating it
* */
object HelloActor {
  def props = Props[HelloActor]
  case class SayHello(name: String)
}

class HelloActor extends Actor {
  import HelloActor._

  def receive = {
    case SayHello(name: String) => sender() ! s"Hello, $name"
  }

}

/* Akka
*
* Akka can work with several containers called actor systems.
* An actor system manages the resources it is configured to use in order to run the actors which it contains
*
* A Play application defines a speical actor system to be used by the application.
* This actor system follows the application life-cycle and restarts automatically when the application restarts
* */

/*
* The most basic things that you can do with an actor is to send it a message
* When you send a message to an actor, there is no response, it's fire and forget
* This is also known as the tell pattern
*
* In a web application however, the tell pattern is often not useful, since HTTP is a protocol that has requests and responses
* In this case, it is much likely that you will want to use the ask pattern. The ask pattern returns a Future, which you can then map to your own result type
* */

/*
* Actors should not be used as a tool for flow control or concurrency.
* They are an effective tool for two purposes - maintaining state and providing a messaging endpoint
* In all other circumstances, it is probably better to use Futures
* */