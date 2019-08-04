package org.worldpay.offer.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.worldpay.offer.api.utils.OfferStatus;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

/**
 * This class serves as a DTO definition for both create and get flows
 */
public class OfferDTO {
    private String product;
    private LocalDateTime start;
    private LocalDateTime end;
    private OfferStatus status;
    private String description;
    private Currency currency;
    private BigDecimal price;


    @Size(max = 50)
    @NotEmpty
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    @Size(max = 255)
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

    @Min(0)
    @NotNull
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OfferDTO{" +
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
