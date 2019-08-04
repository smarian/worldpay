package org.worldpay.offer.api.utils;

import org.springframework.stereotype.Component;
import org.worldpay.offer.api.data.Offer;
import org.worldpay.offer.api.dto.OfferDTO;
import org.worldpay.offer.api.dto.UpdateOfferDTO;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.Optional;

/**
 * This class will map DTO to offers and back
 */
@Component
public class DtoMapper {

    /**
     * Represents updates to an offer via a dto
     * That means: Any field present(aka not null aka send) in the dto will overwrite the offer one
     * (and that means not updated fields remain the same)
     * However if the dto doesn't refer to same product, will do nothing (And such thing should not happen)
     *
     * @param dto dto from which we extract data
     * @param offer  Offer to update
     * @return same Offer as a convenience
     */
    public Offer dtoUpdateOffer(UpdateOfferDTO dto, Offer offer) {
        if (Objects.isNull(offer) || !offer.getProduct().equals(dto.getProduct())) {
            return offer;
        }
        //we update the offer if it's case
        offer.setDescription(Optional.ofNullable(dto.getDescription()).orElse(offer.getDescription()));
        offer.setStart(Optional.ofNullable(dto.getStart()).orElse(LocalDateTime.now()));
        offer.setEnd(Optional.ofNullable(dto.getStart()).orElse(offer.getEnd()));
        offer.setStart(Optional.ofNullable(dto.getStart()).orElse(offer.getStart()));
        offer.setStatus(Optional.ofNullable(dto.getStatus()).orElse(offer.getStatus()));
        return offer;
    }

    /**
     * Create a NEW offer from a dto
     * As such, there will be some defaults in case of dto not providing values:
     *  - description as Product
     *  - start datetime as now
     *  - status as ACTIVE
     *  - Currency as GDP
     *
     * @param dto offer create dto
     * @return an offer object
     */
    public Offer dtoCreateOffer(OfferDTO dto){

        Offer offer = new Offer(){{
            setCurrency(Optional.ofNullable(dto.getCurrency()).orElse(Currency.getInstance("GBP")));
            setStart(Optional.ofNullable(dto.getStart()).orElse(LocalDateTime.now()));
            setEnd(dto.getEnd());
            setDescription(Optional.ofNullable(dto.getDescription()).orElse(dto.getProduct()));
            setProduct(dto.getProduct());
            setStatus(Optional.ofNullable(dto.getStatus()).orElse(OfferStatus.ACTIVE));
            setPrice(dto.getPrice());
        }};

        return offer;
    }

    /**
     * Translates an offer to an OfferDTO object
     *
     * @param offer offer object
     * @return the corresponding OfferDTO (suitable for get responses)
     */

    public OfferDTO offer2Dto(Offer offer){
        OfferDTO dto = new OfferDTO(){{
            setProduct(offer.getProduct());
            setDescription(offer.getDescription());
            setStart(offer.getStart());
            setEnd(offer.getEnd());
            setStatus(offer.getStatus());
            setCurrency(offer.getCurrency());
            setPrice(offer.getPrice());

        }};
        return dto;
    }
}
