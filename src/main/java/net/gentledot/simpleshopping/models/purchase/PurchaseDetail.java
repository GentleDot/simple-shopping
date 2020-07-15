package net.gentledot.simpleshopping.models.purchase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;

import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.checkExpression;


@Entity(name = "detail")
@Table(name = "purchase_detail")
public class PurchaseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @GeneratedValue
    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Column(name = "goods_id", length = 220, nullable = false)
    private String goodsId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected PurchaseDetail() {
    }

    public PurchaseDetail(String goodsId, int quantity) {
        this(null, null, goodsId, quantity);
    }

    private PurchaseDetail(Long seq, Long purchaseId, String goodsId, int quantity) {
        checkExpression(StringUtils.isNotBlank(goodsId), "상품 ID는 반드시 존재해야 합니다.");
        checkExpression(goodsId.getBytes(StandardCharsets.UTF_8).length <= 220, "ID는 220bytes를 넘을 수 없습니다.");
        checkExpression(quantity > 0, "0 이하의 수량은 허용되지 않습니다.");

        this.seq = seq == null ? 0 : seq;
        this.purchaseId = purchaseId == null ? 0 : purchaseId;
        this.goodsId = goodsId;
        this.quantity = quantity;
    }

    public Long getSeq() {
        return seq;
    }

    @JsonIgnore
    public Long getPurchaseId() {
        return purchaseId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PurchaseDetail that = (PurchaseDetail) o;

        return new EqualsBuilder()
                .append(seq, that.seq)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(seq)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("seq", seq)
                .append("goodsId", goodsId)
                .append("quantity", quantity)
                .toString();
    }
}
