package net.gentledot.simpleshopping.repositories.purchase;

import net.gentledot.simpleshopping.models.purchase.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findAllByEmail(String email);
    // 상태 별 조회가 필요한 경우 상태 값을 추가하는 조회를 추가 구현
//    List<Purchase> findAllByEmailAndStatus(String email, String status);
}
