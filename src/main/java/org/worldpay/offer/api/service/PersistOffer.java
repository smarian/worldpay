package org.worldpay.offer.api.service;

import org.springframework.stereotype.Component;
import org.worldpay.offer.api.data.Offer;
import org.worldpay.offer.api.utils.OfferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class plays the role of low level access to persistence
 * Persistence is done in memory data structure and, because it needs it, will use thread safe data storage
 *
 */
@Component
public class PersistOffer {

    private final static ConcurrentHashMap offers = new ConcurrentHashMap<String, Object>(50);

    public List<Offer> getAllOffers() {
        return (List<Offer>) offers.values().stream().collect(Collectors.toList());
    }

    /**
     * This persist an offer
     * The offer is supposed to be validated
     * The offer replace the previous offer if one exists due the key
     *
     * @param offer Offer to persist. It will replace any existing dor same product
     */
    public void addOrReplaceOffer(Offer offer) {
        offers.put(offer.getProduct(), offer);
    }

    public Offer getOffer(String product) {
        return (Offer) offers.get(product);
    }

    public void removeByName(String product) {
        offers.remove(product);
    }
}
