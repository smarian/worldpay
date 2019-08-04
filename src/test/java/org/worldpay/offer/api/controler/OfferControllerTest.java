package org.worldpay.offer.api.controler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.worldpay.offer.api.data.Offer;
import org.worldpay.offer.api.dto.UpdateOfferDTO;
import org.worldpay.offer.api.service.OfferService;
import org.worldpay.offer.api.service.PersistOffer;
import org.worldpay.offer.api.utils.DtoMapper;
import org.worldpay.offer.api.utils.OfferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OfferControllerTest {
    private final static ConcurrentHashMap offers = new ConcurrentHashMap<String, Object>(50);

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OfferService offerServiceMock;

    @MockBean
    PersistOffer persistOffer;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DtoMapper dtoMapper;

    private String BASE_URL = "/offer";

    private Offer offer;
    private Offer offerEndsInThePast;
    private Offer offerEndsBeforeStart;

    private UpdateOfferDTO goodUpdate;
    private UpdateOfferDTO wrongUpdate;
    private UpdateOfferDTO notExistUpdate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        offer = new Offer() {{
            setProduct("test apple");
            setDescription("This is a test");
            setCurrency(Currency.getInstance("RON"));
            setStart(LocalDateTime.now());
            setEnd(LocalDateTime.now().plusHours(15));
            setStatus(OfferStatus.ACTIVE);
            setPrice(new BigDecimal("22.89"));
        }};


        offerEndsInThePast = new Offer() {{
            setProduct("test apple");
            setDescription("This is a test");
            setCurrency(Currency.getInstance("RON"));
            setStart(LocalDateTime.now().minusHours(2));
            setEnd(LocalDateTime.now().minusHours(1));
            setStatus(OfferStatus.ACTIVE);
            setPrice(new BigDecimal("22.89"));
        }};

        offerEndsBeforeStart = new Offer() {{
            setProduct("test apple");
            setDescription("This is a test");
            setCurrency(Currency.getInstance("RON"));
            setStart(LocalDateTime.now().plusHours(12));
            setEnd(LocalDateTime.now().plusHours(10));
            setStatus(OfferStatus.ACTIVE);
            setPrice(new BigDecimal("22.89"));
        }};

        wrongUpdate = new UpdateOfferDTO() {{
            setProduct("test apple");
            setStatus(OfferStatus.ACTIVE);
        }};

        goodUpdate = new UpdateOfferDTO() {{
            setProduct("test apple");
            setStatus(OfferStatus.CANCELED);
        }};

        notExistUpdate = new UpdateOfferDTO() {{
            setProduct("test apple not there");
            setStatus(OfferStatus.CANCELED);
        }};

        offers.put(offer.getProduct(), offer);
    }

    @Test
    public void createValidOffer() throws Exception {
        String jsonContent = mapper.writeValueAsString(dtoMapper.offer2Dto(offer));
        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void createDuplicateValidOffer() throws Exception {
        Mockito.when(persistOffer.getOffer(offer.getProduct())).thenReturn(offer);
        String jsonContent = mapper.writeValueAsString(dtoMapper.offer2Dto(offer));
        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void createOfferEndsInThePast() throws Exception {
        String jsonContent = mapper.writeValueAsString(dtoMapper.offer2Dto(offerEndsInThePast));
        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createOfferEndsBeforeStart() throws Exception {
        String jsonContent = mapper.writeValueAsString(dtoMapper.offer2Dto(offerEndsBeforeStart));
        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllOffers() throws Exception {
        String jsonContent = mapper.writeValueAsString(offers.values().stream().map(v -> dtoMapper.offer2Dto((Offer) v)).collect(Collectors.toList()));

        Mockito.when(persistOffer.getAllOffers()).thenReturn((List<Offer>) offers.values().stream().collect(Collectors.toList()));

        this.mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    public void getOffer() throws Exception {
        Mockito.when(persistOffer.getOffer(offer.getProduct())).thenReturn(offer);

        this.mockMvc.perform(get(BASE_URL + "/" + offer.getProduct()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dtoMapper.offer2Dto(offer))));
    }

    @Test
    public void getOfferWhenWrongPath() throws Exception {
        this.mockMvc.perform(get(BASE_URL + "/not gonna find"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateOfferFail() throws Exception {
        Mockito.when(persistOffer.getOffer(offer.getProduct())).thenReturn(offer);
        String jsonContent = mapper.writeValueAsString(wrongUpdate);
        this.mockMvc.perform(patch(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateOfferSuccess() throws Exception {
        Mockito.when(persistOffer.getOffer(offer.getProduct())).thenReturn(offer);
        String jsonContent = mapper.writeValueAsString(goodUpdate);
        this.mockMvc.perform(patch(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public void updateInexistingOffer() throws Exception {
        Mockito.when(persistOffer.getOffer(offer.getProduct())).thenReturn(offer);
        String jsonContent = mapper.writeValueAsString(notExistUpdate);
        this.mockMvc.perform(patch(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOfferInForbiddenStatus() throws Exception {
        Mockito.when(persistOffer.getOffer(offer.getProduct())).thenReturn(offer);
        this.mockMvc.perform(delete(BASE_URL + "/" + offer.getProduct()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteOfferSuccess() throws Exception {
        this.mockMvc.perform(delete(BASE_URL + "/not there" ))
                .andDo(print())
                .andExpect(status().isOk());
   }

}
