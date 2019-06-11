package counter

import akka.remote.testkit.MultiNodeConfig
import com.typesafe.config.ConfigFactory


object CounterClusterSpecConfig extends MultiNodeConfig {
  val config1 = ConfigFactory.parseString("akka.remote.netty.tcp.port=2551").
    //withFallback(ConfigFactory.load())
  withFallback(ConfigFactory.load("CounterClusterSpec_application"))

  val config2 = ConfigFactory.parseString("akka.remote.netty.tcp.port=2552").
    //withFallback(ConfigFactory.load())
    withFallback(ConfigFactory.load("CounterClusterSpec_application"))

  val node1 = role("node1")
  nodeConfig(node1)(config1)

  val node2 = role("node2")
  nodeConfig(node2)(config2)

}
