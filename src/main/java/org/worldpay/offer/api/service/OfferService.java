package org.worldpay.offer.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.worldpay.offer.api.data.Offer;
import org.worldpay.offer.api.utils.OfferStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


/**
 * Knows handle/process Offer objects
 */
@Component
public class OfferService {

    private PersistOffer persist;

    @Autowired
    public void setPersist(PersistOffer p) {
        persist = p;
    }

    public Offer getOffer(String product) {
        Offer offer = persist.getOffer(product);
        if (Objects.isNull(offer)) return null;
        if (offer.getEnd().compareTo(LocalDateTime.now()) < 0 && offer.getStatus().equals(OfferStatus.ACTIVE))
            offer.setStatus(OfferStatus.EXPIRED);
        if (offer.getStart().compareTo(LocalDateTime.now()) < 0 && offer.getEnd().compareTo(LocalDateTime.now()) > 0 && offer.getStatus().equals(OfferStatus.INACTIVE))
            offer.setStatus(OfferStatus.ACTIVE);
        persist.addOrReplaceOffer(offer);
        return offer;
    }

    public List<Offer> getOfferList() {
        return persist.getAllOffers();
    }

    public void createOffer(Offer offer) {
        persist.addOrReplaceOffer(offer);
    }

    public void replaceOffer(Offer offer) {
        persist.addOrReplaceOffer(offer);
    }

    public void deleteOfferByName(String name) {
        persist.removeByName(name);
    }

    public Boolean canCreateOffer(Offer offer) {
        Offer oldOffer = persist.getOffer(offer.getProduct());
        if (!Objects.isNull(oldOffer) && oldOffer.getStatus().equals(OfferStatus.ACTIVE))
            return false;
        return true;
    }

}
