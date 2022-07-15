package hello.jbdc.connection

import hello.jbdc.Log
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.DriverManager
import javax.sql.DataSource
import com.zaxxer.hikari.HikariDataSource


internal class ConnectionTest: Log {

  @Test
  fun driverManager(){
    val con1 =  DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD)
    val con2 =  DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD)
    logger.info("connection={}, class={}", con1, con1.javaClass)
    logger.info("connection={}, class={}", con2, con2.javaClass)
  }

  @Test
  fun dataSourceDriverManager(){
    val dataSource = DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD)
    useDataSource(dataSource);
  }

  @Test
  fun dataSourceConnectionPool(){
    val dataSource = HikariDataSource().apply {
      jdbcUrl = ConnectionConst.URL
      username = ConnectionConst.USERNAME
      password = ConnectionConst.PASSWORD
      maximumPoolSize = 10
      poolName = "MyPool"
    }

    useDataSource(dataSource)
    Thread.sleep(1000)
  }
  private fun useDataSource(dataSource: DataSource){
    val con1 =  dataSource.connection
    val con2 =  dataSource.connection
    logger.info("connection={}, class={}", con1, con1.javaClass)
    logger.info("connection={}, class={}", con2, con2.javaClass)
  }
}