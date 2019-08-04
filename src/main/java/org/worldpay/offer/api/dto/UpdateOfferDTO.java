package org.worldpay.offer.api.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.worldpay.offer.api.utils.OfferStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * This class express an updating offer DTO
 * As an offer update, a merchant is allowed to change only the active, start or end date and description.
 * Actual Terms of offer are not allowed to be change (aka price and currency)
 * Additional restrictions/validations might happen along flow
 *
 */
public class UpdateOfferDTO {

    private String product;
    private LocalDateTime start;
    private LocalDateTime end;
    private OfferStatus status;
    private String description;

    @NotNull
    @Size(max = 50)
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UpdateOfferDTO{" +
                "product='" + product + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
