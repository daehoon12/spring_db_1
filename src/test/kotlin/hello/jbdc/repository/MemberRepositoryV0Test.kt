package hello.jbdc.repository

import hello.jbdc.Log
import hello.jbdc.domain.Member
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test


internal class MemberRepositoryV0Test: Log{

  private val repository = MemberRepositoryV0()

  @Test
  fun crud(){
    // save
    val member = Member("memberV0", 10000);
    repository.save(member)

    // findById
    val findMember = repository.findById(member.memberId)
    logger.info("findMember = {}", findMember)
    assertThat(findMember).isEqualTo(member)

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