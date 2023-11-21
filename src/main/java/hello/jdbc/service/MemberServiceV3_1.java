package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
* Transaction - Transaction Manager
*/
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String from, String to, int money) {
        //begin transaction
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            bizLogic(from, to, money);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
        //no need to release connection manually (handled by transactionManager)
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
