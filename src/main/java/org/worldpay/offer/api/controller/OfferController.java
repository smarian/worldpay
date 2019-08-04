package org.worldpay.offer.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.worldpay.offer.api.data.Offer;
import org.worldpay.offer.api.dto.OfferDTO;
import org.worldpay.offer.api.dto.UpdateOfferDTO;
import org.worldpay.offer.api.service.OfferService;
import org.worldpay.offer.api.utils.DtoMapper;
import org.worldpay.offer.api.utils.OfferStatus;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Validated
@Controller
public class OfferController {

    private OfferService service;
    private DtoMapper mapper;
    private final Logger logger =  LoggerFactory.getLogger(OfferController.class);
    private static final String BASE_URL = "/offer";
    @Autowired
    public void setService(OfferService s)
    {
        service = s;
    }

    @Autowired
    public void setMapper(DtoMapper m){mapper = m;}


    /**
     * The intent is to map a generic endpoint to serve the list and a specific offer
     * @param product id of product
     * @return Response entity
     */
    @RequestMapping(value = {BASE_URL,BASE_URL+"/{product}" },
            produces = { "application/json" },
            method = RequestMethod.GET)
    ResponseEntity getOffer(@Validated @PathVariable(value="product", required = false) @Size(max=50) String product)
    {
        Object response;
        if (Objects.isNull(product)){
            logger.info("Processing an offer list request");
            response = service.getOfferList().stream().map(v -> mapper.offer2Dto(v)).collect(Collectors.toList());
        }else {
            logger.info("Request to get offer " + product);
            Offer offer = service.getOffer(product);
            if (Objects.isNull(offer))
                return notFoundResponse();
            response = mapper.offer2Dto(service.getOffer(product));
        }

        return new ResponseEntity<>(response ,HttpStatus.OK);
    }

    @RequestMapping(value = BASE_URL,
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    ResponseEntity createOffer(@Valid @RequestBody OfferDTO offerDto)
    {
        logger.info("Request to create offer " + offerDto.getProduct());

        Offer newOffer = mapper.dtoCreateOffer(offerDto);
        if(!service.canCreateOffer(newOffer)) {
            logger.error("Already existing offer for " + newOffer.getProduct());
            return conflictResponse();
        }
        service.createOffer(newOffer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = BASE_URL,
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.PATCH)
    ResponseEntity updateOffer(@Valid @RequestBody UpdateOfferDTO offerDto, final Errors errors) {

        logger.info("Request to update offer " + offerDto.getProduct());

        Offer oldOffer = service.getOffer(offerDto.getProduct());

        if(Objects.isNull(oldOffer) ){
            logger.error("Offer not found");
            return notFoundResponse();
        }
        if(errors.hasErrors()){
            logger.error("Validation errors" + errors.toString());
            return new ResponseEntity<>(parseError(errors), HttpStatus.BAD_REQUEST);
        }

        Offer modifiedOffer = mapper.dtoUpdateOffer(offerDto, oldOffer);
        service.replaceOffer(modifiedOffer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = BASE_URL+"/{product}",
            produces = { "application/json" },
            method = RequestMethod.DELETE)

    ResponseEntity deleteOffer(@Valid @PathVariable(value="product", required = true) @Size(max=50) String product)  {
        logger.info("Request to remove offer " + product);
        Offer offer = service.getOffer(product);
        if(!Objects.isNull(offer) && offer.getStatus().equals(OfferStatus.ACTIVE))
            return deleteForbiddenResponse();

        service.deleteOfferByName(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private HashMap<String, Object> parseError(Errors errors){
        HashMap<String, Object> response = new HashMap<String, Object>(){{
            put("timestamp", OffsetDateTime.now());
            put("error", "Validation errors");

            ArrayList<HashMap<String, Object>> details = new ArrayList<HashMap<String, Object>>();
            details.addAll(
                    errors.getFieldErrors().stream().map(e -> new HashMap<String,Object>(){{
                        put("reason", e.getDefaultMessage());
                        put("field", e.getField());
                        put("rejected", e.getRejectedValue());
                    }}).collect(Collectors.toList())
            );

            details.addAll(
                    errors.getGlobalErrors().stream().map(e -> new HashMap<String,Object>(){{
                        put("reason", e.getDefaultMessage());
                    }}).collect(Collectors.toList())
            );

            put("details",details);

        }};
        return response;
    }

    private ResponseEntity notFoundResponse(){
        return  new ResponseEntity<>(
                new HashMap<String, Object>(){{
                    put("timestamp", OffsetDateTime.now());
                    put("error", "Product not found");

                }},
                HttpStatus.NOT_FOUND);
    }

    private ResponseEntity conflictResponse(){
        return  new ResponseEntity<>(
                new HashMap<String, Object>(){{
                    put("timestamp", OffsetDateTime.now());
                    put("error", "There is an already active offer on product");

                }},
                HttpStatus.CONFLICT);
    }

    private ResponseEntity deleteForbiddenResponse(){
        return  new ResponseEntity<>(
                new HashMap<String, Object>(){{
                    put("timestamp", OffsetDateTime.now());
                    put("error", "Cannot delete an active offer");

                }},
                HttpStatus.FORBIDDEN);
    }
}
