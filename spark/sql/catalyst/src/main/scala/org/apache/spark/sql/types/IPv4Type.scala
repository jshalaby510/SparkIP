/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.types
import org.apache.spark.sql.catalyst.util.{ArrayData, GenericArrayData}

@SQLUserDefinedType(udt = classOf[IPv4TypeUDT])
private[sql] class IPv4Type(val x: Double, val y: Double) extends Serializable {

  override def hashCode(): Int = 31 * (31 * x.hashCode()) + y.hashCode()

  override def equals(other: Any): Boolean = other match {
    case that: IPv4Type => this.x == that.x && this.y == that.y
    case _ => false
  }

  override def toString(): String = s"($x, $y)"
}

/**
 * User-defined type for [[ExamplePoint]].
 */
private[sql] class IPv4TypeUDT extends UserDefinedType[IPv4Type] {

  override def sqlType: DataType = ArrayType(DoubleType, false)

//  override def pyUDT: String = "pyspark.testing.sqlutils.ExamplePointUDT"

  override def serialize(p: IPv4Type): GenericArrayData = {
    val output = new Array[Any](2)
    output(0) = p.x
    output(1) = p.y
    new GenericArrayData(output)
  }

  override def deserialize(datum: Any): IPv4Type = {
    datum match {
      case values: ArrayData =>
        new IPv4Type(values.getDouble(0), values.getDouble(1))
    }
  }

  override def userClass: Class[IPv4Type] = classOf[IPv4Type]

  private[spark] override def asNullable: IPv4TypeUDT = this
}