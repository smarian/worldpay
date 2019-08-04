package org.worldpay.offer.api.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.worldpay.offer.api.dto.OfferDTO;
import org.worldpay.offer.api.dto.UpdateOfferDTO;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class is intended to test relationship between start, end dates ans offers status
 * in a nut shell:
 * - an offer can't be active if now is not in the start - end interval
 * - an offer can't end in past time
 * - an offer can't have start > end
 * - when updating an offer, the only status available to (eventually change) is CANCELED
 */

@Component
public class OfferTimesAndStatusValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return OfferDTO.class.isAssignableFrom(aClass) || UpdateOfferDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        if (!supports(o.getClass())) return;
        LocalDateTime start = null;
        LocalDateTime end = null;
        OfferStatus status = null;
        boolean isUpdate = false;

        if (o instanceof OfferDTO) {
            start = ((OfferDTO) o).getStart();
            end = ((OfferDTO) o).getEnd();
            status = ((OfferDTO) o).getStatus();
        }

        if (o instanceof UpdateOfferDTO) {
            start = ((UpdateOfferDTO) o).getStart();
            end = ((UpdateOfferDTO) o).getEnd();
            status = ((UpdateOfferDTO) o).getStatus();
            isUpdate = true;
        }

        if (!Objects.isNull(end) && end.compareTo(LocalDateTime.now()) < 0) {
            errors.rejectValue("end", "", "End time cannot be in the past");
        }

        if (!Objects.isNull(start) && !Objects.isNull(end) && end.compareTo(start) < 0) {
            errors.rejectValue("start", "", "Start time cannot be later than end time");
        }

        if (!Objects.isNull(status) && status.equals(OfferStatus.ACTIVE) &&
                !Objects.isNull(end) && !Objects.isNull(start) &&
                (start.compareTo(LocalDateTime.now()) > 0 || end.compareTo(LocalDateTime.now()) < 0)
        ) {
            errors.rejectValue("status", "", "status conflicts for start/end time");
        }

        if (!Objects.isNull(status) && isUpdate && !status.equals(OfferStatus.CANCELED)) {
            errors.rejectValue("status", "", "CANCELED is the  only modification to status allowed");
        }

    }
}
