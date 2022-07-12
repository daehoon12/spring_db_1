package hello.jbdc.connection

import hello.jbdc.Log
import java.sql.*


class DBConnectionUtil(
) {
  companion object: Log {
    fun getConnection(): Connection {
      try {
        val connection =
          DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD)
          logger.info("get connection={}, class={}", connection, connection.javaClass)
          return connection
      } catch (e: SQLException) {
          throw IllegalStateException(e)
      }
    }
  }
}