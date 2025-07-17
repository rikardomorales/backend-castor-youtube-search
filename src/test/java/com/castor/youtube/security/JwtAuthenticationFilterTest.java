package com.castor.youtube.security;

import com.castor.youtube.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = "valid.jwt.token";
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(jwtUtil.extractUsername(validToken)).thenReturn("testUser");
        Mockito.when(jwtUtil.validateToken(Mockito.eq(validToken), Mockito.any(UserDetails.class))).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
    }

    @Test
    void shouldAllowAccessWithValidToken() throws Exception {
        mockMvc.perform(get("/protected-endpoint")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/protected-endpoint"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDenyAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/protected-endpoint")
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isForbidden());
    }
}