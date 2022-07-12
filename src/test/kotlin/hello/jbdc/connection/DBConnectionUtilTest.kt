package hello.jbdc.connection


import hello.jbdc.connection.DBConnectionUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class DBConnectionUtilTest {

  @Test
  fun connection(){
    val connection = DBConnectionUtil.getConnection()
    assertThat(connection).isNotNull();
  }
}