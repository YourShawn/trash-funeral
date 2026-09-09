package com.trashfuneral;

import com.trashfuneral.auth.config.SecurityConfig;
import com.trashfuneral.funeral.TrashFuneralApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CorsOriginPatternParseTest {

    @Test
    void splitsTrimsAndFallsBackToLocalhostDefaults() {
        assertThat(SecurityConfig.parseOriginPatterns(null))
                .containsExactly("http://localhost:*", "http://127.0.0.1:*");
        assertThat(SecurityConfig.parseOriginPatterns("  "))
                .containsExactly("http://localhost:*", "http://127.0.0.1:*");
        assertThat(SecurityConfig.parseOriginPatterns("http://localhost:*, http://127.0.0.1:*, http://deploy.example:*"))
                .containsExactly("http://localhost:*", "http://127.0.0.1:*", "http://deploy.example:*");
    }
}

@SpringBootTest(classes = TrashFuneralApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DefaultCorsSmokeTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void localhostOriginCanLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"demo123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void loopbackOriginPreflightIsAllowed() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://127.0.0.1:80")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://127.0.0.1:80"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS));
    }

    @Test
    void unknownHostOriginIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://deploy.example")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"demo123\"}"))
                .andExpect(status().isForbidden());
    }
}

@SpringBootTest(
        classes = TrashFuneralApplication.class,
        properties = "app.cors.allowed-origin-patterns=http://localhost:*,http://127.0.0.1:*,http://deploy.example,http://deploy.example:*"
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeployHostCorsSmokeTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void configuredDeployOriginCanLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://deploy.example")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"demo123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://deploy.example"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void configuredDeployOriginWithPortCanLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://deploy.example:8088")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"demo123\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://deploy.example:8088"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }
}
