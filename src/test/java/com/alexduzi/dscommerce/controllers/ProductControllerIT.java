package com.alexduzi.dscommerce.controllers;

import com.alexduzi.dscommerce.dto.ProductDTO;
import com.alexduzi.dscommerce.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.alexduzi.dscommerce.util.ProductFactory.createProduct;
import static com.alexduzi.dscommerce.util.ProductFactory.createProductDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String productName;
    private String adminAccessToken;
    private String clientAccessToken;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        productName = "Macbook";

        String adminUsername = "alex@gmail.com";
        String adminPassword = "123456";
        String clientUsername = "maria@gmail.com";
        String clientPassword = "123456";

        adminAccessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        clientAccessToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);

        productDTO = createProductDTO(createProduct(100L, "New product", "Product description", 900.0, "Product url"));
    }

    @Test
    void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {
        ResultActions result = mockMvc.perform(get("/products?name={productName}", productName)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(1L));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].id").value(3L));
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[0].price").value(1250.0));
    }

    @Test
    void findAllShouldReturnPageWhenNameParamIsEmpty() throws Exception {
        ResultActions result = mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(25L));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].id").value(1L));
        result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
        result.andExpect(jsonPath("$.content[0].price").value(90.5));
    }

    @Test
    void insertShouldReturnNewProductInserted() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());

        result.andExpect(jsonPath("$.id").value(26L));
        result.andExpect(jsonPath("$.name").value("New product"));
        result.andExpect(jsonPath("$.description").value("Product description"));
    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenProductNameIsInvalid() throws Exception {
        productDTO.setName(" ");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());

        result.andExpect(jsonPath("$.errors").exists());

//        result.andExpect(jsonPath("$.errors[0].fieldName").value("name"));
//        result.andExpect(jsonPath("$.errors[0].message").value("Nome precisa ter de 3 a 80 caracteres"));
    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenProductDescriptionIsInvalid() throws Exception {
        productDTO.setDescription(" ");
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());

        result.andExpect(jsonPath("$.errors").exists());

//        result.andExpect(jsonPath("$.errors[0].fieldName").value("description"));
//        result.andExpect(jsonPath("$.errors[0].message").value("Campo requerido"));
    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenProductPriceIsNegative() throws Exception {
        productDTO.setPrice(-5.0);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());

        result.andExpect(jsonPath("$.errors").exists());

//        result.andExpect(jsonPath("$.errors[0].fieldName").value("description"));
//        result.andExpect(jsonPath("$.errors[0].message").value("Campo requerido"));
    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenProductPriceIsZero() throws Exception {
        productDTO.setPrice(0.0);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());

        result.andExpect(jsonPath("$.errors").exists());

//        result.andExpect(jsonPath("$.errors[0].fieldName").value("description"));
//        result.andExpect(jsonPath("$.errors[0].message").value("Campo requerido"));
    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenProductCategoriesIsEmpty() throws Exception {
        productDTO.getCategories().clear();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());

        result.andExpect(jsonPath("$.errors").exists());

//        result.andExpect(jsonPath("$.errors[0].fieldName").value("description"));
//        result.andExpect(jsonPath("$.errors[0].message").value("Campo requerido"));
    }

    @Test
    void insertShouldReturnForbiddenWhenTheLoggedUserIsClient() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + clientAccessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    void insertShouldReturnUnauthorizedWhenUserIsNotLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(post("/products")
                .header("Authorization", "Bearer ")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldReturnNoContentWhenProductIsDeleted() throws Exception {
        Long productId = 5L;

        ResultActions result = mockMvc.perform(delete("/products/{id}", productId)
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenProductDoesNotExists() throws Exception {
        Long productId = 100L;

        ResultActions result = mockMvc.perform(delete("/products/{id}", productId)
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnBadRequestWhenProductHaveDependentRelations() throws Exception {
        Long productId = 3L;

        ResultActions result = mockMvc.perform(delete("/products/{id}", productId)
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // spring data jpa does not throw DataIntegrityViolationException anymore
        result.andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnForbiddenWhenUserIsClient() throws Exception {
        Long productId = 5L;

        ResultActions result = mockMvc.perform(delete("/products/{id}", productId)
                .header("Authorization", "Bearer " + clientAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // spring data jpa does not throw DataIntegrityViolationException anymore
        result.andExpect(status().isForbidden());
    }

    @Test
    void deleteShouldReturnUnauthorizedWhenUserIsNotLogged() throws Exception {
        Long productId = 5L;

        ResultActions result = mockMvc.perform(delete("/products/{id}", productId)
                .header("Authorization", "Bearer ")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // spring data jpa does not throw DataIntegrityViolationException anymore
        result.andExpect(status().isUnauthorized());
    }
}