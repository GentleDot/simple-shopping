package net.gentledot.simpleshopping.services.purchase;

import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.purchase.Purchase;
import net.gentledot.simpleshopping.models.purchase.PurchaseDetail;
import net.gentledot.simpleshopping.models.purchase.PurchaseStatus;
import net.gentledot.simpleshopping.models.response.purchase.PurchaseResponse;
import net.gentledot.simpleshopping.repositories.book.BookRepository;
import net.gentledot.simpleshopping.repositories.purchase.PurchaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BookRepository bookRepository;
    private final PurchaseRepository purchaseRepository;

    public PurchaseService(BookRepository bookRepository, PurchaseRepository purchaseRepository) {
        this.bookRepository = bookRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public PurchaseResponse addPurchase(Email email, Map<String, Integer> items) {
        Purchase newOrder = new Purchase.Builder(email.getAddress(), new ArrayList<>()).build();
        items.forEach((goods, quantity) -> {
            bookRepository.findbyBookId(goods)
                    .orElseThrow(() -> new RuntimeException("해당 id의 상품이 없습니다."));
            newOrder.addGoodsInItems(new PurchaseDetail(goods, quantity));
        });

        Purchase savedPurchase = purchaseRepository.save(newOrder);
        log.debug("저장된 주문 내역 : {}", savedPurchase);

        return new PurchaseResponse.Builder()
                .id(savedPurchase.getId())
                .lastChangeAt(savedPurchase.getLastChangeAt())
                .purchaseDate(savedPurchase.getCreateAt())
                .status(savedPurchase.getStatus())
                .build();
    }

    public PurchaseResponse cancelPurchase(Email email, Long id) {
        Purchase getPurchaseById = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당하는 ID의 주문 내역이 존재하지 않습니다."));

        if (!getPurchaseById.getEmailAdress().equals(email.getAddress())) {
            throw new RuntimeException("로그인한 사용자의 주문 내역이 아닙니다.");
        }

        getPurchaseById.setStatusAndLastChangeAt(PurchaseStatus.CANCEL);
        Purchase savedPurchase = purchaseRepository.save(getPurchaseById);
        log.debug("저장된 주문 내역 : {}", savedPurchase);

        return new PurchaseResponse.Builder()
                .id(savedPurchase.getId())
                .lastChangeAt(savedPurchase.getLastChangeAt())
                .purchaseDate(savedPurchase.getCreateAt())
                .status(savedPurchase.getStatus())
                .build();
    }

    public List<Purchase> getPurchasesList(Email email) {
        return purchaseRepository.findAllByEmail(email.getAddress());
    }
}
