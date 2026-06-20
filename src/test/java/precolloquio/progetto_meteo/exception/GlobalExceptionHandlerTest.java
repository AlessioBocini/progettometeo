package precolloquio.progetto_meteo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test/rest-client-exception")
        public void throwRestClientException() {
            throw new RestClientException("Connessione interrotta");
        }

        @GetMapping("/test/illegal-state")
        public void throwIllegalState() {
            throw new IllegalStateException("Stato non valido");
        }

        @GetMapping("/test/illegal-argument")
        public void throwIllegalArgument() {
            throw new IllegalArgumentException("Argomento non valido");
        }
    }

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void handleRestClientException_ShouldReturnBadGateway() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/test/rest-client-exception"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value("Errore di comunicazione con il provider meteo esterno: Connessione interrotta"));
    }

    @Test
    public void handleIllegalState_ShouldReturnInternalServerError() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Stato non valido"));
    }

    @Test
    public void handleIllegalArgument_ShouldReturnBadRequest() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Argomento non valido"));
    }
}