package com.databricks115
import org.apache.spark.sql.types.DataType

case class IPNetwork (addr: String) extends DataType with IPConversions with IPValidation {
  //to extend DataType
  override def asNullable(): DataType = this
  override def defaultSize(): Int = 1

  //parse IPv4 and subnet
  private def parseNetwork(ip: String): (String, Int) = {
    //1.1.1.1/16 format
    if(ip.matches("""([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d)""")){
      val pattern = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d)""".r
      val pattern(o1, o2, o3, o4, o5) = ip
      //validation check
      require(IPv4Validation(List(o1, o2, o3, o4)) && o5.toInt >= 1 && o5.toInt <= 32, "Network is invalid.")
      (s"$o1.$o2.$o3.$o4", o5.toInt)
    }
      //1.1.1.1/255.255.0.0 format
    else if(ip.matches("""([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""")){
      val pattern = """([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\/([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
      val pattern(o1, o2, o3, o4, o5, o6, o7, o8) = ip
      //validation check
      require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid.")
      (s"$o1.$o2.$o3.$o4", subnetToCidr(s"$o5.$o6.$o7.$o8"))
    }
      //Address 1.1.1.1 Netmask 255.255.255.0 format
    else if(ip.matches("""(^Address )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})( Netmask )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""")){
      val pattern = """(^Address )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})( Netmask )([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})\.([0-9]|[1-9]\d{1,2})""".r
      val pattern(s1, o1, o2, o3, o4, s2, o5, o6, o7, o8) = ip
      //validation check
      require(IPv4Validation(List(o1, o2, o3, o4, o5, o6, o7, o8)), "Network is invalid.")
      (s"$o1.$o2.$o3.$o4", subnetToCidr(s"$o5.$o6.$o7.$o8"))
    }
      //if its in an invalid format
    else throw new Exception
  }
  private val parsedAddr: (String, Int) = parseNetwork(addr)

  //start and end of the network
  private val addrLStart: Long = 0xFFFFFFFF << (32 - parsedAddr._2) & IPv4ToLong(parsedAddr._1)
  private val addrLEnd: Long = addrLStart + math.pow(2, 32-parsedAddr._2).toLong - 1
  //range of the network
  val range: String = s"${longToIPv4(addrLStart)}-${longToIPv4(addrLEnd)}"

  //compare networks
  def ==(that: IPNetwork): Boolean = this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd
  def !=(that: IPNetwork): Boolean = this.addrLStart != that.addrLStart || this.addrLEnd != that.addrLEnd
  def <(that: IPNetwork): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd)
  }
  def >(that: IPNetwork): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd)
  }
  def <=(that: IPNetwork): Boolean = {
    this.addrLStart < that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd < that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }
  def >=(that: IPNetwork): Boolean = {
    this.addrLStart > that.addrLStart ||
      (this.addrLStart == that.addrLStart && this.addrLEnd > that.addrLEnd) ||
      (this.addrLStart == that.addrLStart && this.addrLEnd == that.addrLEnd)
  }

  //checks if an IP is in the network
  def netContainsIP(ip: IPAddress): Boolean = if (ip.addrL >= addrLStart && ip.addrL <= addrLEnd) true else false

}
