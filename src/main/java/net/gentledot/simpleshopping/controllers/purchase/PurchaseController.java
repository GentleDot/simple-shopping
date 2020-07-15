package net.gentledot.simpleshopping.controllers.purchase;

import net.gentledot.simpleshopping.controllers.BaseController;
import net.gentledot.simpleshopping.models.member.Email;
import net.gentledot.simpleshopping.models.purchase.Purchase;
import net.gentledot.simpleshopping.models.request.BuyRequest;
import net.gentledot.simpleshopping.models.response.ApiResult;
import net.gentledot.simpleshopping.models.response.purchase.PurchaseResponse;
import net.gentledot.simpleshopping.services.purchase.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class PurchaseController extends BaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    /*
     * POST api/v1/purchase/add 요청 시 주문 내역을 저장
     * 형식은 {"order": {"txb202003testgoods" : "1"}}
     * 등록된 상품이 아닌 ID가 요청으로 전송되는 경우 저장 처리되지 않음.
     * */
    @PostMapping(path = "purchase/add")
    public ApiResult<PurchaseResponse> addPurchase(@RequestBody BuyRequest request, Principal account) {
        return ApiResult.ok(purchaseService.addPurchase(new Email(account.getName()), request.getOrder()));
    }

    @PutMapping(path = "purchase/cancel/{id}")
    public ApiResult<PurchaseResponse> cancelPurchase(@PathVariable(value = "id") Long purchaseId, Principal account) {
        log.debug("로그인 정보 확인 : {}", account);
        return ApiResult.ok(purchaseService.cancelPurchase(new Email(account.getName()), purchaseId));
    }

    @GetMapping(path = "member/myPurchases")
    public ApiResult<List<Purchase>> getMyPurchaseList(Principal account) {
        log.debug("로그인 정보 확인 : {}", account);
        return ApiResult.ok(purchaseService.getPurchasesList(new Email(account.getName())));
    }
}
