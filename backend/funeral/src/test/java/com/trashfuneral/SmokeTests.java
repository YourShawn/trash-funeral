package com.trashfuneral;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trashfuneral.funeral.TrashFuneralApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrashFuneralApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSmokeTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void registerLoginAndMe() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String body = """
                {"username":"%s","email":"%s@example.com","password":"secret1","displayName":"Mourner"}
                """.formatted(username, username);

        MvcResult register = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.token").isString())
                .andReturn();

        String token = mapper.readTree(register.getResponse().getContentAsString()).path("data").path("token").asText();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"secret1\"}".formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.username").value(username));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"wrong\"}".formatted(username)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username));
    }

    @Test
    void duplicateUsernameRejected() throws Exception {
        String username = "dup_" + UUID.randomUUID().toString().substring(0, 8);
        String body = """
                {"username":"%s","email":"%s@example.com","password":"secret1"}
                """.formatted(username, username);
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        String otherEmail = """
                {"username":"%s","email":"%s2@example.com","password":"secret1"}
                """.formatted(username, username);
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(otherEmail))
                .andExpect(status().isConflict());
    }
}

@SpringBootTest(classes = TrashFuneralApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FuneralFlowSmokeTest {

    private static final byte[] TINY_PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
    );

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private String token;

    @BeforeEach
    void loginDemo() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"demo123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        token = mapper.readTree(result.getResponse().getContentAsString()).path("data").path("token").asText();
        assertThat(token).isNotBlank();
    }

    @Test
    void catalogAlmanacAndHealthArePublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mockMvc.perform(get("/api/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.objectTypes").isArray())
                .andExpect(jsonPath("$.data.music").isArray())
                .andExpect(jsonPath("$.data.flowers").isArray());
        mockMvc.perform(get("/api/almanac").param("objectType", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.disclaimerZh").isString())
                .andExpect(jsonPath("$.data.whereToThrowZh").isString());
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void identifyCreateEditCompleteCemeteryAndPublicCard() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "relic.png", "image/png", TINY_PNG);
        MvcResult identified = mockMvc.perform(multipart("/api/funerals/identify")
                        .file(photo)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.photoId").isString())
                .andExpect(jsonPath("$.data.mock").value(true))
                .andExpect(jsonPath("$.data.objectTypeCode").isString())
                .andReturn();

        JsonNode idData = mapper.readTree(identified.getResponse().getContentAsString()).path("data");
        String photoId = idData.path("photoId").asText();
        String type = idData.path("objectTypeCode").asText();
        String eulogy = idData.path("suggestedEulogyZh").asText();
        String music = idData.path("suggestedMusic").asText();
        String flowers = idData.path("suggestedFlowers").asText();

        String createJson = mapper.createObjectNode()
                .put("photoId", photoId)
                .put("objectTypeCode", type)
                .put("objectName", "测试遗物")
                .put("identifiedLabel", idData.path("label").asText())
                .put("eulogy", eulogy)
                .put("musicCode", music)
                .put("flowersCode", flowers)
                .put("locale", "zh")
                .toString();

        MvcResult created = mockMvc.perform(post("/api/funerals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.almanac.disclaimerEn").isString())
                .andReturn();

        JsonNode funeral = mapper.readTree(created.getResponse().getContentAsString()).path("data");
        long id = funeral.path("id").asLong();
        String publicToken = funeral.path("publicToken").asText();

        mockMvc.perform(put("/api/funerals/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eulogy\":\"改过的悼词，谢谢你曾在抽屉里发光。\",\"musicCode\":\"canon_can\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eulogy").value("改过的悼词，谢谢你曾在抽屉里发光。"))
                .andExpect(jsonPath("$.data.musicCode").value("canon_can"));

        mockMvc.perform(get("/api/public/cards/" + publicToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/funerals/" + id + "/complete")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        mockMvc.perform(get("/api/funerals").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value((int) id))
                .andExpect(jsonPath("$.data[0].objectName").value("测试遗物"));

        mockMvc.perform(get("/api/public/cards/" + publicToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.objectName").value("测试遗物"))
                .andExpect(jsonPath("$.data.eulogy").value("改过的悼词，谢谢你曾在抽屉里发光。"));

        mockMvc.perform(get("/api/files/" + photoId)).andExpect(status().isOk());
    }

    @Test
    void identifyRequiresAuth() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "relic.png", "image/png", TINY_PNG);
        mockMvc.perform(multipart("/api/funerals/identify").file(photo))
                .andExpect(status().isUnauthorized());
    }
}
