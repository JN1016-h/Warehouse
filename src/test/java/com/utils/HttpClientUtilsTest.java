package com.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpClientUtils unit tests.
 */
public class HttpClientUtilsTest {

    private HttpServer server;
    private int port;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        port = server.getAddress().getPort();
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testDoGet_success() {
        server.createContext("/get", new EchoHandler("get-ok\nline2"));
        server.start();

        String body = HttpClientUtils.doGet("http://127.0.0.1:" + port + "/get");
        assertNotNull(body);
        assertTrue(body.contains("get-ok"));
    }

    @Test
    public void testDoGet_invalidUrlReturnsNull() {
        assertNull(HttpClientUtils.doGet("http://127.0.0.1:1/not-running"));
    }

    @Test
    public void testDoPost_success() {
        server.createContext("/post", exchange -> {
            byte[] ok = "posted".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, ok.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(ok);
            }
        });
        server.start();

        Map<String, String> param = new HashMap<>();
        param.put("key", "value");
        String body = HttpClientUtils.doPost("http://127.0.0.1:" + port + "/post", param);
        assertEquals("posted", body);
    }

    @Test
    public void testDoPost_nullParams() {
        server.createContext("/post-empty", exchange -> {
            byte[] ok = "empty".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, ok.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(ok);
            }
        });
        server.start();

        String body = HttpClientUtils.doPost("http://127.0.0.1:" + port + "/post-empty", null);
        assertEquals("empty", body);
    }

    private static class EchoHandler implements HttpHandler {
        private final String response;

        EchoHandler(String response) {
            this.response = response;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
