package counter

import akka.actor._
import akka.cluster.sharding.ClusterSharding
import akka.testkit._
import com.typesafe.config.ConfigFactory
import org.scalatest._


class CounterSpec extends TestKit(
    ActorSystem("default", ConfigFactory.load("Local_application")))
  with PersistenceCleanup
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def beforeAll: Unit = {
    deleteStorageLocations()
  }

  override def afterAll: Unit = {
    deleteStorageLocations()
    TestKit.shutdownActorSystem(system)
  }

  Counter.isMemberUp(system)

  "The counter" should {
    "0 at begin" in {
      val counterRegion: ActorRef = ClusterSharding(system).shardRegion(typeName = "Counter")
      counterRegion ! Get(123)
      expectMsg(0)
    }
    "1 after plus 1" in {
      val counterRegion: ActorRef = ClusterSharding(system).shardRegion(typeName = "Counter")
      counterRegion ! EntityEnvelope(123, Increment)
      counterRegion ! Get(123)
      expectMsg(1)
    }
  }
}
