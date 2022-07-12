package hello.jbdc.repository

import hello.jbdc.Log
import hello.jbdc.domain.Member
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class MemberRepositoryV0Test: Log{

  private val memberRepositoryV0 = MemberRepositoryV0()

  @Test
  fun crud(){
    // save
    val member = Member("memberV0", 10000);
    memberRepositoryV0.save(member)

    // findById
    val findMember = memberRepositoryV0.findById(member.memberId)
    logger.info("findMember = {}", findMember)
    assertThat(findMember).isEqualTo(member)

    // update : money (10000 -> 20000)
    memberRepositoryV0.update(member.memberId, 20000)
    val updateMember = memberRepositoryV0.findById(member.memberId)
    assertThat(updateMember.money).isEqualTo(20000)

    // delete
    memberRepositoryV0.delete(member.memberId)

    assertThatThrownBy { memberRepositoryV0.findById(member.memberId) }
      .isInstanceOf(NoSuchElementException::class.java)
  }
}