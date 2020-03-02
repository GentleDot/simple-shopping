package net.gentledot.simpleshopping.repositories.member;

import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member save(Member member);

    @Modifying(clearAutomatically = true)
    @Query("update members m set m.email = :#{#member.getEmail()}, m.password = :#{#member.getPassword()}, " +
            "m.name = :#{#member.getName().orElse(null)}, m.lastLoginAt = :#{#member.getLastLoginAt()} where m.seq = :#{#member.getSeq()}")
    void update(Member member);

    @Query("select m from members m where m.email = :#{#email.address}")
    Optional<Member> findByEmail(Email email);
}
