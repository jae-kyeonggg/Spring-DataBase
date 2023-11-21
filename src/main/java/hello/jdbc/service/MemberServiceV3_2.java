package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/*
* Transaction - Transaction Template
*/
@Slf4j
public class MemberServiceV3_2 {

    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String from, String to, int money) {
        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(from, to, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
