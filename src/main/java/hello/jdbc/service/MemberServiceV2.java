package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
* transaction - connect parameter, pool considered closing
*/
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String from, String to, int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false);   //begin transaction

            bizLogic(con, from, to, money);

            con.commit();   //commit when succeed
        } catch (Exception e) {
            con.rollback(); //rollback when fail
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private void bizLogic(Connection con, String from, String to, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, from);
        Member toMember = memberRepository.findById(con, to);
        memberRepository.update(con, from, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, to, toMember.getMoney() + money);
    }
}
