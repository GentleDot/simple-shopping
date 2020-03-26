package net.gentledot.simpleshopping.models.purchase;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.gentledot.simpleshopping.common.util.argumentHandleUtil.checkExpression;
import static org.apache.commons.lang3.ObjectUtils.*;

@Entity(name = "purchase")
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Column(name = "last_change_at")
    private LocalDateTime lastChangeAt;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "purchase_id")
    private List<PurchaseDetail> items;

    protected Purchase() {
    }

    public Purchase(Long id, String email, PurchaseStatus status, LocalDateTime lastChangeAt, LocalDateTime createAt, List<PurchaseDetail> items) {
        this(id, email, status.getCode(), lastChangeAt, createAt, items);
    }

    private Purchase(Long id, String email, String status, LocalDateTime lastChangeAt, LocalDateTime createAt, List<PurchaseDetail> items) {
        checkExpression(isNotEmpty(email), "이메일은 반드시 존재해야 합니다.");
        checkExpression(StringUtils.isNotBlank(status), "주문 상태는 반드시 존재해야 합니다.");
        checkExpression(allNotNull(items), "주문 상품은 null이 될 수 없습니다.");

        this.id = id == null ? 0 : id;
        this.email = email;
        this.status = status;
        this.lastChangeAt = lastChangeAt;
        this.createAt = defaultIfNull(createAt, LocalDateTime.now());
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public PurchaseStatus getStatus() {
        return PurchaseStatus.getStatus(status);
    }

    public LocalDateTime getLastChangeAt() {
        return lastChangeAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public List<PurchaseDetail> getItems() {
        return items;
    }

    public void addGoodsInItems(PurchaseDetail item) {
        if (items == null) {
            items = new ArrayList<PurchaseDetail>();
        }
        items.add(item);
    }

    public void setStatusAndLastChangeAt(PurchaseStatus status) {
        String code = status.getCode();
        if (!this.status.equals(code)) {
            this.lastChangeAt = LocalDateTime.now();
        }
        this.status = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Purchase purchase = (Purchase) o;

        return new EqualsBuilder()
                .append(id, purchase.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("email", email)
                .append("status", status)
                .append("lastChangeAt", lastChangeAt)
                .append("createAt", createAt)
                .append("주문 상품 수", items.size())
                .toString();
    }


    public static final class Builder {
        private Long id;
        private String email;
        private String status;
        private LocalDateTime lastChangeAt;
        private LocalDateTime createAt;
        private List<PurchaseDetail> items;

        public Builder(String email, List<PurchaseDetail> items) {
            this.email = email;
            this.items = items;
            this.status = PurchaseStatus.CONFIRM.getCode();
        }

        public Builder(Purchase purchase) {
            this.id = purchase.id;
            this.email = purchase.email;
            this.status = purchase.status;
            this.lastChangeAt = purchase.lastChangeAt;
            this.createAt = purchase.createAt;
            this.items = purchase.items;
        }

        public Purchase build() {
            return new Purchase(id, email, status, lastChangeAt, createAt, items);
        }
    }


}
