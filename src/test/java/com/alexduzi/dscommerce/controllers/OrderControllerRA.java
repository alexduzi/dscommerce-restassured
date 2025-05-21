package com.alexduzi.dscommerce.controllers;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.alexduzi.dscommerce.util.TokenUtil.obtainAccessToken;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OrderControllerRA {

    private Long existingOrderId, nonExistingOrderId;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;


    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        existingOrderId = 1L;
        nonExistingOrderId = 100L;

        clientToken = obtainAccessToken(clientUsername, clientPassword);
        adminToken = obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLoggedAndOrderDoesNotBelongToUser() {
        Long otherOrderId = 2L;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", otherOrderId)
                .then()
                .statusCode(403);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnUnauthorized() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(401);
    }
}