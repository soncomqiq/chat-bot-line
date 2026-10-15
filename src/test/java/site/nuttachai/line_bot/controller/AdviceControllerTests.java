package site.nuttachai.line_bot.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.nuttachai.line_bot.exception.InvalidLineKeyException;
import site.nuttachai.line_bot.exception.LineKeyLoadingException;
import site.nuttachai.line_bot.exception.LineJwtGenerationException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdviceControllerTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new AdviceController())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/key-loading", "/invalid-key"})
    void returnsSafeProblemDetailForKeyFailures(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("The LINE signing key is unavailable."))
                .andExpect(jsonPath("$.instance").value(path))
                .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void returnsSafeProblemDetailForJwtGenerationFailure() throws Exception {
        mockMvc.perform(get("/jwt-generation"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Unable to generate the LINE JWT."))
                .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void returnsSafeProblemDetailForUnexpectedFailures() throws Exception {
        mockMvc.perform(get("/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred."))
                .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void preservesBadRequestForMissingParameter() throws Exception {
        mockMvc.perform(get("/parameter"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void preservesBadRequestForInvalidParameterType() throws Exception {
        mockMvc.perform(get("/parameter").param("value", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void preservesMethodNotAllowedAndAllowHeader() throws Exception {
        mockMvc.perform(post("/parameter"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(header().string("Allow", containsString("GET")));
    }

    @RestController
    static class TestController {

        @GetMapping("/key-loading")
        public String keyLoadingFailure() {
            throw new LineKeyLoadingException("secret file path");
        }

        @GetMapping("/invalid-key")
        public String invalidKeyFailure() {
            throw new InvalidLineKeyException("secret key material");
        }

        @GetMapping("/jwt-generation")
        public String jwtGenerationFailure() {
            throw new LineJwtGenerationException("secret signing details");
        }

        @GetMapping("/unexpected")
        public String unexpectedFailure() {
            throw new IllegalStateException("secret internal details");
        }

        @GetMapping("/parameter")
        public int parameter(@RequestParam("value") int value) {
            return value;
        }
    }
}
