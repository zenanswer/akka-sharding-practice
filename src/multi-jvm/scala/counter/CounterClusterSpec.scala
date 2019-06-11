package counter

import scala.concurrent.duration._
import akka.actor.{ActorRef}
import akka.cluster.sharding.ClusterSharding
import akka.testkit.ImplicitSender
import akka.remote.testkit.MultiNodeSpec


class WordsClusterSpecMultiJvmNode1 extends CounterClusterSpec
class WordsClusterSpecMultiJvmNode2 extends CounterClusterSpec

class CounterClusterSpec extends MultiNodeSpec(CounterClusterSpecConfig)
  with PersistenceCleanup
  with STMultiNodeSpec
  with ImplicitSender {

  import CounterClusterSpecConfig._

  def initialParticipants = roles.size

  val node1Address = node(node1).address
  val node2Address = node(node2).address

  //muteDeadLetters(classOf[Any])(system)

  override def atStartup(): Unit = {
    deleteStorageLocations()
  }

  override def afterTermination(): Unit = {
    deleteStorageLocations()
  }

  "A Counter cluster" must {

    "form the cluster" in within(10 seconds) {
      runOn(node1) {
        Counter.isMemberUp(system)
        enterBarrier("startup")
      }
      runOn(node2) {
        Counter.isMemberUp(system)
        enterBarrier("startup")
      }
      enterBarrier("cluster-startup")
    }

    "both of node is 0" in within(10 seconds) {
      runOn(node1) {
        val counterRegion: ActorRef = ClusterSharding(system).shardRegion(typeName = "Counter")
        counterRegion ! Get(123)
        expectMsg(0)
        enterBarrier("0 at begin")
      }
      runOn(node2) {
        val counterRegion: ActorRef = ClusterSharding(system).shardRegion(typeName = "Counter")
        counterRegion ! Get(123)
        expectMsg(0)
        enterBarrier("0 at begin")
      }
    }

    "node1 plus 1 and then node2 get it" in within(10 seconds) {
      runOn(node1) {
        val counterRegion: ActorRef = ClusterSharding(system).shardRegion(typeName = "Counter")
        counterRegion ! EntityEnvelope(123, Increment)
        counterRegion ! Get(123)
        expectMsg(1)
        enterBarrier("node1 plus 1")
      }

      runOn(node2) {
        enterBarrier("node1 plus 1")
        Counter.isMemberUp(system)
        val counterRegion: ActorRef = ClusterSharding(system).shardRegion(typeName = "Counter")
        counterRegion ! Get(123)
        expectMsg(1)
      }

      enterBarrier("finished")
    }
  }
}
