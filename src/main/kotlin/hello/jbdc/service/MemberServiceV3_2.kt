package hello.jbdc.service

import hello.jbdc.Log
import hello.jbdc.domain.Member
import hello.jbdc.repository.MemberRepositoryV3
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.sql.Connection
import java.sql.SQLException
import kotlin.jvm.Throws

/*
  트랜잭션 - 트랜잭션 매니저
 */

class MemberServiceV3_2(
  private val transactionManager: PlatformTransactionManager,
  private val memberRepository: MemberRepositoryV3

) :Log{
  val txTemplate: TransactionTemplate

  init{
    txTemplate = TransactionTemplate(transactionManager)
  }

  @Throws(SQLException::class)
  fun accountTransfer(fromId: String, toId: String, money: Int){
    // 트랜잭션 시작
    txTemplate.executeWithoutResult {
      try{
        bizLogic(fromId, toId, money)
      } catch (e: SQLException){
        throw IllegalStateException(e)
      }
    }
  }

  @Throws(SQLException::class)
  private fun bizLogic(fromId: String, toId: String, money: Int){
    val fromMember = memberRepository.findById(fromId)
    val toMember = memberRepository.findById(toId)

    memberRepository.update(fromId, fromMember.money - money)
    validation(toMember)
    memberRepository.update(toId, fromMember.money + money)
  }

  private fun validation(toMember: Member){
    if (toMember.memberId.equals("ex")){
      throw IllegalStateException("이체 중 예외 발생")
    }
  }


}