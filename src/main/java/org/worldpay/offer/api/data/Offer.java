package org.worldpay.offer.api.data;

import org.worldpay.offer.api.utils.OfferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

/**
 * Serves as the application representation of an offer
 *
 */
public class Offer {

    private String product;
    private LocalDateTime start;
    private LocalDateTime end;
    private OfferStatus status;
    private String description;
    private Currency currency;
    private BigDecimal price;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "product='" + product + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", currency=" + currency +
                ", price=" + price +
                '}';
    }
}
