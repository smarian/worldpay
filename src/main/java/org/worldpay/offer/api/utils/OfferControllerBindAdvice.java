package org.worldpay.offer.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.worldpay.offer.api.controller.OfferController;

/**
 * this class "advices" Spring data binder that we have additional things (in this case validators)
 * to add into the soup
 *
 */
@RestControllerAdvice(assignableTypes = {OfferController.class})
public class OfferControllerBindAdvice {
    private OfferTimesAndStatusValidator offerTSV;
    @Autowired
    public void setOfferTSV(OfferTimesAndStatusValidator oTSV){
        offerTSV = oTSV;
    }

    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        binder.addValidators(offerTSV);
    }

}
