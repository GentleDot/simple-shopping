package net.gentledot.simpleshopping.models.response.purchase;

import net.gentledot.simpleshopping.models.purchase.PurchaseStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

public class PurchaseResponse {
    private Long id;
    private LocalDateTime purchaseDate;
    private PurchaseStatus status;
    private LocalDateTime lastChangeAt;

    public PurchaseResponse(Long id, LocalDateTime purchaseDate, PurchaseStatus status, LocalDateTime lastChangeAt) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.status = status;
        this.lastChangeAt = lastChangeAt;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastChangeAt() {
        return lastChangeAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("purchaseDate", purchaseDate)
                .append("status", status)
                .append("lastChangeAt", lastChangeAt)
                .toString();
    }

    public static final class Builder {
        private Long id;
        private LocalDateTime purchaseDate;
        private PurchaseStatus status;
        private LocalDateTime lastChangeAt;

        public Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder purchaseDate(LocalDateTime purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder status(PurchaseStatus status) {
            this.status = status;
            return this;
        }

        public Builder lastChangeAt(LocalDateTime lastChangeAt) {
            this.lastChangeAt = lastChangeAt;
            return this;
        }

        public PurchaseResponse build() {
            return new PurchaseResponse(id, purchaseDate, status, lastChangeAt);
        }
    }
}
