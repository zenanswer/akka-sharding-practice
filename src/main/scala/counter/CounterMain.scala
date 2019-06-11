package counter

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.cluster.sharding.ClusterSharding
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object CounterMain extends App {
  var port = args(0).toInt

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
    withFallback(ConfigFactory.load())

  // Create an Akka system
  val system = ActorSystem("default", config)

  Counter.isMemberUp(system)

  if (port == 2551) {
    Thread.sleep(2000)
    implicit val timeout = Timeout(5 seconds)
    val counterRegion: ActorRef = ClusterSharding(system).shardRegion("Counter")
    val future = counterRegion ? Get(123)
    val result = Await.result(future, timeout.duration).asInstanceOf[Integer]
    println(result)
  }
}
