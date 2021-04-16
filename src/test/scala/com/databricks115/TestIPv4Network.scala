package com.databricks115
import org.scalatest.FunSuite

class TestIPv4Network extends FunSuite {
  test("Network CIDR Constructor - failure") {
    assertThrows[Exception] {
      val net = IPv4Network("192.161.150.78/21")
    }
  }
  
  test("Network contains cidr notation - success") {
    val net = IPv4Network("192.161.144.0/21")
    val ip = IPv4("192.161.145.1")
    assert(net.contains(ip))
  }

  test("Network contains cidr notation - failure") {
    val net = IPv4Network("191.161.144.0/21")
    val ip = IPv4("192.161.145.1")
    assert(!net.contains(ip))
  }

  test("Network contains subnet mask notation - success") {
    val net = IPv4Network("192.161.144.0/255.255.248.0")
    val ip = IPv4("192.161.145.1")
    assert(net.contains(ip))
  }

  test("Network contains subnet mask notation - failure") {
    val net = IPv4Network("192.161.144.0/255.255.248.0")
    val ip = IPv4("191.161.145.1")
    assert(!net.contains(ip))
  }

  test("Network contains range notation - success") {
    val net = IPv4Network("191.161.144.1-191.161.151.254")
    val ip = IPv4("191.161.150.1")
    assert(net.contains(ip))
  }

  test("Network contains range notation - failure") {
    val net = IPv4Network("191.161.144.1-191.161.151.254")
    val ip = IPv4("192.161.145.1")
    assert(!net.contains(ip))
  }

  test("Get network address") {
    val net = IPv4Network("191.161.0.0/16")
    assert(net.networkAddress === IPv4("191.161.0.0"))
  }

  test("Get broadcast address") {
    val net = IPv4Network("191.161.0.0/16")
    assert(net.broadcastAddress === IPv4("191.161.255.255"))
  }

  test("Network == - success") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.2.0/24")
    assert(net == ip)
  }

  test("Network == - failure") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.2.0/23")
    assert(!(net == ip))
  }

  test("Network != - success") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.2.0/23")
    assert(net != ip)
  }

  test("Network != - failure") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.2.0/24")
    assert(!(net != ip))
  }

  test("Network < - success 1") {
    val net = IPv4Network("192.0.0.0/16")
    val ip = IPv4Network("192.0.2.0/24")
    assert(net < ip)
  }

  test("Network < - success 2") {
    val net = IPv4Network("192.0.0.0/18")
    val ip = IPv4Network("192.0.0.0/16")
    assert(net < ip)
  }

  test("Network < - failure 1") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.0.0/16")
    assert(!(net < ip))
  }

  test("Network < - failure 2") {
    val net = IPv4Network("192.0.0.0/16")
    val ip = IPv4Network("192.0.0.0/18")
    assert(!(net < ip))
  }

  test("Network > - success 1") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.0.0/16")
    assert(net > ip)
  }

  test("Network > - success 2") {
    val net = IPv4Network("192.0.0.0/16")
    val ip = IPv4Network("192.0.0.0/18")
    assert(net > ip)
  }

  test("Network > - failure 1") {
    val net = IPv4Network("192.0.0.0/16")
    val ip = IPv4Network("192.0.2.0/24")
    assert(!(net > ip))
  }

  test("Network > - failure 2") {
    val net = IPv4Network("192.0.0.0/18")
    val ip = IPv4Network("192.0.0.0/16")
    assert(!(net > ip))
  }

  test("Network <= - success 1") {
    val net = IPv4Network("192.0.0.0/16")
    val ip = IPv4Network("192.0.2.0/24")
    assert(net <= ip)
  }

  test("Network <= - success 2") {
    val net = IPv4Network("192.0.0.0/18")
    val ip = IPv4Network("192.0.0.0/18")
    assert(net <= ip)
  }

  test("Network <= - failure 1") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.0.0/16")
    assert(!(net <= ip))
  }

  test("Network <= - failure 2") {
    val net = IPv4Network("192.0.0.0/16")
    val ip = IPv4Network("192.0.0.0/18")
    assert(!(net <= ip))
  }

  test("Network >= - success 1") {
    val net = IPv4Network("192.0.2.0/24")
    val ip = IPv4Network("192.0.0.0/16")
    assert(net >= ip)
  }

  test("Network >= - success 2") {
    val net = IPv4Network("192.0.0.0/18")
    val ip = IPv4Network("192.0.0.0/18")
    assert(net >= ip)
  }

  test("Network >= - failure 1") {
    val net = IPv4Network("192.0.0.0/16")
    val ip = IPv4Network("192.0.2.0/24")
    assert(!(net >= ip))
  }

  test("Network >= - failure 2") {
    val net = IPv4Network("192.0.0.0/18")
    val ip = IPv4Network("192.0.0.0/16")
    assert(!(net >= ip))
  }

  test("Networks intersect - success") {
    val net = IPv4Network("192.0.0.0/18")
    val net2 = IPv4Network("192.0.0.0/16")
    assert(net.netsIntersect(net2))
  }

  test("Networks intersect - failure") {
    val net = IPv4Network("191.0.0.0/18")
    val net2 = IPv4Network("192.0.0.0/16")
    assert(!net.netsIntersect(net2))
  }
}
