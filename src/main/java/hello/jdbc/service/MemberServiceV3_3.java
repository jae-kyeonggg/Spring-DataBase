package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/*
* Transaction - @Transactional AOP
*/
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String from, String to, int money) throws SQLException {
        bizLogic(from, to, money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void bizLogic(String from, String to, int money) throws SQLException {
        Member fromMember = memberRepository.findById(from);
        Member toMember = memberRepository.findById(to);
        memberRepository.update(from, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(to, toMember.getMoney() + money);
    }
}
