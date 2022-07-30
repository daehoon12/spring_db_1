package hello.jbdc.service

import hello.jbdc.Log
import hello.jbdc.connection.ConnectionConst
import hello.jbdc.domain.Member
import hello.jbdc.repository.MemberRepositoryV3
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.SQLException


@SpringBootTest
internal class MemberServiceV3_3Test: Log{

  val MEMBER_A = "memberA"
  val MEMBER_B = "memberB"
  val MEMBER_EX = "ex"

  @Autowired
  lateinit var memberRepository: MemberRepositoryV3

  @Autowired
  lateinit var memberService: MemberServiceV3_3


  @AfterEach
  @Throws(SQLException::class)
  fun after(){
    memberRepository.delete(MEMBER_A);
    memberRepository.delete(MEMBER_B);
    memberRepository.delete(MEMBER_EX);
  }

  @TestConfiguration
   class TestConfig{
    @Bean
    fun dataSource() = DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD)

    @Bean
    fun transactionManager() = DataSourceTransactionManager(dataSource())

    @Bean
    fun memberRepositoryV3() = MemberRepositoryV3(dataSource())

    @Bean
    fun memberServiceV3_3() = MemberServiceV3_3(memberRepositoryV3())
  }

  @Test
  fun AopCheck() {
    logger.info("memberService class={}", memberService.javaClass)
    logger.info("memberRepository class={}", memberRepository.javaClass)
    Assertions.assertThat(AopUtils.isAopProxy(memberService)).isTrue()
    Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse()
  }

  @Test
  @DisplayName("정상 이체")
  @Throws(SQLException::class)
  fun accountTransfer() {
    //given
    val memberA = Member(MEMBER_A, 10000)
    val memberB = Member(MEMBER_B, 10000)
    memberRepository.save(memberA)
    memberRepository.save(memberB)
    //when
    memberService.accountTransfer(memberA.memberId,
      memberB.memberId, 2000)
//    //then
    val findMemberA: Member = memberRepository.findById(memberA.memberId)
    val findMemberB: Member = memberRepository.findById(memberB.memberId)
    assertThat(findMemberA.money).isEqualTo(8000)
    assertThat(findMemberB.money).isEqualTo(12000)
  }

  @Test
  @DisplayName("이체 중 예외 발생")
  fun accountTransferEx(){
    //given
    val memberA = Member(MEMBER_A, 10000)
    val memberEx = Member(MEMBER_EX, 10000)
    memberRepository.save(memberA)
    memberRepository.save(memberEx)
    // when

    assertThatThrownBy {
      memberService.accountTransfer(memberA.memberId, memberEx.memberId,
        2000)
    }
      .isInstanceOf(IllegalStateException::class.java)

    // then
    val findMemberA = memberRepository.findById(memberA.memberId)
    val findMemberEx = memberRepository.findById(memberEx.memberId)

    assertThat(findMemberA.money).isEqualTo(10000)
    assertThat(findMemberEx.money).isEqualTo(10000)

  }
}