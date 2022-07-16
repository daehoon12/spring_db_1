package hello.jbdc.service

import hello.jbdc.connection.ConnectionConst
import hello.jbdc.domain.Member
import hello.jbdc.repository.MemberRepositoryV2
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.SQLException


internal class MemberServiceV2Test{

  val MEMBER_A = "memberA"
  val MEMBER_B = "memberB"
  val MEMBER_EX = "ex"

  lateinit var memberRepository: MemberRepositoryV2
  lateinit var  memberService: MemberServiceV2

  @BeforeEach
  fun before(){
    val dataSource = DriverManagerDataSource(ConnectionConst.URL,
      ConnectionConst.USERNAME, ConnectionConst.PASSWORD)

    memberRepository = MemberRepositoryV2(dataSource)
    memberService = MemberServiceV2(dataSource, memberRepository)
  }

  @AfterEach
  @Throws(SQLException::class)
  fun after(){
    memberRepository.delete(MEMBER_A);
    memberRepository.delete(MEMBER_B);
    memberRepository.delete(MEMBER_EX);
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