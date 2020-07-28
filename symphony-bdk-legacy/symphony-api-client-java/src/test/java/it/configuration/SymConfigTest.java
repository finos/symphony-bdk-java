package it.configuration;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class SymConfigTest {

    private SymConfig config;

    @Before
    public void setUp() {
        InputStream configStream = SymConfigTest.class.getResourceAsStream("/bot-config.json");
        config = SymConfigLoader.load(configStream);
    }

    @Test
    public void getAgentUrlTest() {
        assertEquals(config.getAgentUrl(), "https://localhost:7443");
        config.setAgentContextPath("");
        assertEquals(config.getAgentUrl(), "https://localhost:7443");
        config.setAgentContextPath("/ctx");
        assertEquals(config.getAgentUrl(), "https://localhost:7443/ctx");
        config.setAgentContextPath("/ctx1/ctx2");
        assertEquals(config.getAgentUrl(), "https://localhost:7443/ctx1/ctx2");
        config.setAgentContextPath("ctx");
        assertEquals(config.getAgentUrl(), "https://localhost:7443/ctx");
        config.setAgentContextPath("ctx/");
        assertEquals(config.getAgentUrl(), "https://localhost:7443/ctx");
    }

    @Test
    public void getPodUrlTest() {
        assertEquals(config.getPodUrl(), "https://localhost:7443");
        config.setPodContextPath("");
        assertEquals(config.getPodUrl(), "https://localhost:7443");
        config.setPodContextPath("/ctx");
        assertEquals(config.getPodUrl(), "https://localhost:7443/ctx");
        config.setPodContextPath("/ctx1/ctx2");
        assertEquals(config.getPodUrl(), "https://localhost:7443/ctx1/ctx2");
        config.setPodContextPath("ctx");
        assertEquals(config.getPodUrl(), "https://localhost:7443/ctx");
        config.setPodContextPath("ctx/");
        assertEquals(config.getPodUrl(), "https://localhost:7443/ctx");
    }

    @Test
    public void getSessionAuthTest() {
        assertEquals(config.getSessionAuthUrl(), "https://localhost:7443");
        config.setSessionAuthContextPath("");
        assertEquals(config.getSessionAuthUrl(), "https://localhost:7443");
        config.setSessionAuthContextPath("/ctx");
        assertEquals(config.getSessionAuthUrl(), "https://localhost:7443/ctx");
        config.setSessionAuthContextPath("/ctx1/ctx2");
        assertEquals(config.getSessionAuthUrl(), "https://localhost:7443/ctx1/ctx2");
        config.setSessionAuthContextPath("ctx");
        assertEquals(config.getSessionAuthUrl(), "https://localhost:7443/ctx");
        config.setSessionAuthContextPath("ctx/");
        assertEquals(config.getSessionAuthUrl(), "https://localhost:7443/ctx");
    }

    @Test
    public void getKeyAuthTest() {
        assertEquals(config.getKeyAuthUrl(), "https://localhost:7443");
        config.setKeyAuthContextPath("");
        assertEquals(config.getKeyAuthUrl(), "https://localhost:7443");
        config.setKeyAuthContextPath("/ctx");
        assertEquals(config.getKeyAuthUrl(), "https://localhost:7443/ctx");
        config.setKeyAuthContextPath("/ctx1/ctx2");
        assertEquals(config.getKeyAuthUrl(), "https://localhost:7443/ctx1/ctx2");
        config.setKeyAuthContextPath("ctx");
        assertEquals(config.getKeyAuthUrl(), "https://localhost:7443/ctx");
        config.setKeyAuthContextPath("ctx/");
        assertEquals(config.getKeyAuthUrl(), "https://localhost:7443/ctx");
    }
}
