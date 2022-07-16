package hello.jbdc.service

import hello.jbdc.domain.Member
import hello.jbdc.repository.MemberRepositoryV1

class MemberServiceV1(
  private val memberRepository: MemberRepositoryV1
) {

  fun accountTransfer(fromId: String, toId: String, money: Int){
    val fromMember = memberRepository.findById(fromId)
    val toMember = memberRepository.findById(toId)

    memberRepository.update(fromId, fromMember.money  - money);
    validation(toMember);
    memberRepository.update(toId, toMember.money + money);
  }

  private fun validation(toMember: Member){
    if (toMember.memberId.equals("ex")){
      throw IllegalStateException("이체 중 예외 발생")
    }
  }

}