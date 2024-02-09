package guru.springframework.spring6reactive.controllers;

import guru.springframework.spring6reactive.model.BeerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(20)
    void testListBeers() {
        webTestClient.get()
                .uri(BeerController.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @Order(10)
    void testGetByid() {
        webTestClient.get()
                .uri(BeerController.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(11)
    void testGetByIdNotFound() {
        webTestClient.get()
                .uri(BeerController.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(30)
    void testCreateBeer() {
        webTestClient.post()
                .uri(BeerController.BEER_PATH)
                .body(Mono.just(getTestBeerDTO()), BeerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost:8080" + BeerController.BEER_PATH + "/4");
    }

    @Test
    @Order(31)
    void testCreateBeerBadData() {
        BeerDTO testBeerDTO = getTestBeerDTO();
        testBeerDTO.setBeerName("");
        webTestClient.post()
                .uri(BeerController.BEER_PATH)
                .body(Mono.just(testBeerDTO), BeerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(40)
    void testUpdateBeer() {
        webTestClient.put()
                .uri(BeerController.BEER_PATH_ID, 1)
                .body(Mono.just(getTestBeerDTO()), BeerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(41)
    void testUpdateBeerBadRequest() {
        BeerDTO testBeerDto = getTestBeerDTO();
        testBeerDto.setBeerStyle("");
        webTestClient.put()
                .uri(BeerController.BEER_PATH_ID, 1)
                .body(Mono.just(testBeerDto), BeerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(50)
    void testPatchBeer() {
        webTestClient.patch()
                .uri(BeerController.BEER_PATH_ID, 1)
                .body(Mono.just(getTestBeerDTO()), BeerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(999)
    void testDeleteBeer() {
        webTestClient.delete()
                .uri(BeerController.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    private BeerDTO getTestBeerDTO() {
        return BeerDTO.builder()
                .beerName("Space Dust")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123213")
                .build();
    }

}