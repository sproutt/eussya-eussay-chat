package com.sproutt.eussyaeussyachat;

import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketConnectionTest {

    @LocalServerPort
    private int port;

    @Autowired
    MessageConverter messageConverter;

    private static final String SEND_ENDPOINT = "/pub/chat/one-to-one";
    private static final String SUBSCRIBE_ENDPOINT = "/sub/chat/";

    private String URL;
    private BlockingQueue<OneToOneChatMessage> blockingQueue;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;


    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        URL = "ws://localhost:" + port + "/websocket";

        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(messageConverter);
        stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);
    }

    @Test
    void testConnection() throws InterruptedException {
        long from = 1l;
        long to = 2l;

        stompSession.subscribe(SUBSCRIBE_ENDPOINT + to, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return OneToOneChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((OneToOneChatMessage) payload);
            }
        });

        String content = "test content";
        stompSession.send(SEND_ENDPOINT, new OneToOneChatMessageDTO(from, to, content));

        OneToOneChatMessage message = blockingQueue.poll(1, SECONDS);

        assertNotNull(message);
        assertEquals(content, message.getContent());
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        return transports;
    }
}

