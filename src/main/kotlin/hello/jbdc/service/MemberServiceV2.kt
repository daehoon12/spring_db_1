package hello.jbdc.service

import hello.jbdc.Log
import hello.jbdc.domain.Member
import hello.jbdc.repository.MemberRepositoryV2
import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException
import kotlin.jvm.Throws

class MemberServiceV2(
  private val dataSource: DataSource,
  private val memberRepository: MemberRepositoryV2,
) :Log{

  @Throws(SQLException::class)
  fun accountTransfer(fromId: String, toId: String, money: Int){
    val con = dataSource.connection
    try {
      con.autoCommit = false
      bizLogic(con, fromId, toId, money)
      con.commit()
    }catch (e: Exception){
      con.rollback()
      throw IllegalStateException(e)
    }finally {
      release(con)
    }
  }

  @Throws(SQLException::class)
  private fun bizLogic(con: Connection, fromId: String, toId: String, money: Int){
    val fromMember = memberRepository.findById(con, fromId)
    val toMember = memberRepository.findById(con, toId)

    memberRepository.update(con, fromId, fromMember.money - money)
    validation(toMember)
    memberRepository.update(con, toId, fromMember.money + money)
  }

  private fun validation(toMember: Member){
    if (toMember.memberId.equals("ex")){
      throw IllegalStateException("이체 중 예외 발생")
    }
  }

  private fun release(con: Connection){
      try{
        con.autoCommit = true
        con.close()
      } catch (e: Exception){
        logger.info("error", e)
    }
  }


}