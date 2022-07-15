package hello.jbdc.repository

import com.zaxxer.hikari.HikariDataSource
import hello.jbdc.Log
import hello.jbdc.connection.ConnectionConst
import hello.jbdc.domain.Member
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MemberRepositoryV1Test: Log {

  lateinit var repository: MemberRepositoryV1

  @BeforeEach
  fun beforeEach(){
    val dataSource = HikariDataSource().apply {
      jdbcUrl = ConnectionConst.URL
      username = ConnectionConst.USERNAME
      password = ConnectionConst.PASSWORD
    }

    repository = MemberRepositoryV1(dataSource)
  }

  @Test
  fun crud(){
    logger.info("start")

    // save
    val member = Member("memberV0", 10000);
    repository.save(member)

    //findById
    val memberById = repository.findById(member.memberId)
    assertThat(memberById).isNotNull()

    // update : money (10000 -> 20000)
    repository.update(member.memberId, 20000)
    val updateMember = repository.findById(member.memberId)
    assertThat(updateMember.money).isEqualTo(20000)

    // delete
    repository.delete(member.memberId)
    assertThatThrownBy { repository.findById(member.memberId) }
      .isInstanceOf(NoSuchElementException::class.java)
  }

}