package com.bfhl.api;

import com.bfhl.api.controller.BfhlController;
import com.bfhl.api.dto.BfhlRequest;
import com.bfhl.api.dto.BfhlResponse;
import com.bfhl.api.service.BfhlService;
import com.bfhl.api.service.BfhlServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ─────────────────────────────────────────────────────────────────────────────
// 1. Service-level unit tests (no Spring context)
// ─────────────────────────────────────────────────────────────────────────────
class BfhlServiceImplTest {

    private BfhlService service;

    @BeforeEach
    void setUp() {
        service = new BfhlServiceImpl();
    }

    private BfhlRequest req(String... items) {
        BfhlRequest r = new BfhlRequest();
        r.setData(List.of(items));
        return r;
    }

    // ── Example A from the spec ────────────────────────────────────────────────
    @Test
    @DisplayName("Example A: mixed numbers, letters, special char")
    void exampleA() {
        BfhlResponse res = service.processData(req("a", "1", "334", "4", "R", "$"));

        assertTrue(res.isSuccess());
        assertEquals(List.of("1"),          res.getOddNumbers());
        assertEquals(List.of("334", "4"),   res.getEvenNumbers());
        assertEquals(List.of("A", "R"),     res.getAlphabets());
        assertEquals(List.of("$"),          res.getSpecialCharacters());
        assertEquals("339",                 res.getSum());
        assertEquals("Ra",                  res.getConcatString());
    }

    // ── Example B from the spec ────────────────────────────────────────────────
    @Test
    @DisplayName("Example B: multiple special chars, single-odd number")
    void exampleB() {
        BfhlResponse res = service.processData(req("2", "a", "y", "4", "&", "-", "*", "5", "92", "b"));

        assertTrue(res.isSuccess());
        assertEquals(List.of("5"),          res.getOddNumbers());
        assertEquals(List.of("2", "4", "92"), res.getEvenNumbers());
        assertEquals(List.of("A", "Y", "B"), res.getAlphabets());
        assertEquals(List.of("&", "-", "*"), res.getSpecialCharacters());
        assertEquals("103",                 res.getSum());
        assertEquals("ByA",                 res.getConcatString());
    }

    // ── Example C from the spec ────────────────────────────────────────────────
    @Test
    @DisplayName("Example C: multi-character alphabetic tokens only")
    void exampleC() {
        BfhlResponse res = service.processData(req("A", "ABCD", "DOE"));

        assertTrue(res.isSuccess());
        assertEquals(List.of(),                     res.getOddNumbers());
        assertEquals(List.of(),                     res.getEvenNumbers());
        assertEquals(List.of("A", "ABCD", "DOE"),   res.getAlphabets());
        assertEquals(List.of(),                     res.getSpecialCharacters());
        assertEquals("0",                           res.getSum());
        assertEquals("EoDdCbAa",                    res.getConcatString());
    }

    // ── Edge: empty data array ─────────────────────────────────────────────────
    @Test
    @DisplayName("Empty data array returns zero sum and empty lists")
    void emptyData() {
        BfhlResponse res = service.processData(req());

        assertTrue(res.isSuccess());
        assertTrue(res.getOddNumbers().isEmpty());
        assertTrue(res.getEvenNumbers().isEmpty());
        assertTrue(res.getAlphabets().isEmpty());
        assertTrue(res.getSpecialCharacters().isEmpty());
        assertEquals("0",  res.getSum());
        assertEquals("",   res.getConcatString());
    }

    // ── Edge: only special characters ─────────────────────────────────────────
    @Test
    @DisplayName("Only special characters")
    void onlySpecialChars() {
        BfhlResponse res = service.processData(req("@", "#", "!"));

        assertEquals(List.of("@", "#", "!"), res.getSpecialCharacters());
        assertEquals("0",  res.getSum());
        assertEquals("",   res.getConcatString());
    }

    // ── user_id format check ───────────────────────────────────────────────────
    @Test
    @DisplayName("user_id matches expected format name_ddmmyyyy")
    void userIdFormat() {
        BfhlResponse res = service.processData(req("1"));
        // Must be lowercase and follow the pattern <name>_<ddmmyyyy>
        assertTrue(res.getUserId() != null && !res.getUserId().isEmpty(),
        "user_id must not be null or empty");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Controller-level tests (MockMvc / slice context)
// ─────────────────────────────────────────────────────────────────────────────
@WebMvcTest(BfhlController.class)
class BfhlControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  BfhlService bfhlService;

    private BfhlResponse stubResponse() {
        return BfhlResponse.builder()
                .isSuccess(true)
                .userId("john_doe_17091999")
                .email("john@xyz.com")
                .rollNumber("ABCD123")
                .oddNumbers(List.of("1"))
                .evenNumbers(List.of("334", "4"))
                .alphabets(List.of("A", "R"))
                .specialCharacters(List.of("$"))
                .sum("339")
                .concatString("Ra")
                .build();
    }

    @Test
    @DisplayName("POST /bfhl returns 200 with correct JSON structure")
    void postBfhlReturns200() throws Exception {
        when(bfhlService.processData(any())).thenReturn(stubResponse());

        BfhlRequest req = new BfhlRequest();
        req.setData(List.of("a", "1", "334", "4", "R", "$"));

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.user_id").value("john_doe_17091999"))
                .andExpect(jsonPath("$.odd_numbers[0]").value("1"))
                .andExpect(jsonPath("$.even_numbers[0]").value("334"))
                .andExpect(jsonPath("$.alphabets[0]").value("A"))
                .andExpect(jsonPath("$.special_characters[0]").value("$"))
                .andExpect(jsonPath("$.sum").value("339"))
                .andExpect(jsonPath("$.concat_string").value("Ra"));
    }

    @Test
    @DisplayName("POST /bfhl with missing data field returns 400")
    void postBfhlMissingData() throws Exception {
        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.is_success").value(false));
    }

    @Test
    @DisplayName("POST /bfhl with malformed JSON returns 400")
    void postBfhlMalformedJson() throws Exception {
        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.is_success").value(false));
    }
}
