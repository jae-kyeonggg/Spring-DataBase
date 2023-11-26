package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/*
* remove "SQLException"
*/
@Slf4j
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    public MemberServiceV4(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String from, String to, int money) {
        bizLogic(from, to, money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void bizLogic(String from, String to, int money) {
        Member fromMember = memberRepository.findById(from);
        Member toMember = memberRepository.findById(to);
        memberRepository.update(from, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(to, toMember.getMoney() + money);
    }
}
