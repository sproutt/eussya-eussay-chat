package com.sproutt.eussyaeussyachat;

import com.sproutt.eussyaeussyachat.api.dto.OneToOneChatMessageDTO;
import com.sproutt.eussyaeussyachat.domain.ChatMessageRepository;
import com.sproutt.eussyaeussyachat.domain.OneToOneChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
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
    private MessageConverter messageConverter;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private RedisTemplate<String, OneToOneChatMessage> redisTemplate;

    private static final String SEND_ENDPOINT = "/pub/chat/one-to-one";
    private static final String SUBSCRIBE_ENDPOINT = "/sub/chat/";

    private String URL;
    private BlockingQueue<OneToOneChatMessage> blockingQueue;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private long from;
    private long to;


    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        URL = "ws://localhost:" + port + "/websocket";
        from = 1l;
        to = 2l;

        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(messageConverter);
        stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

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

        redisTemplate.delete(OneToOneChatMessage.ROOM_ID_PREFIX + from + "-" + to);
    }

    @Test
    void testWebsocketConnection() throws InterruptedException {
        String content = "test content";
        stompSession.send(SEND_ENDPOINT, new OneToOneChatMessageDTO(from, to, content));

        OneToOneChatMessage message = blockingQueue.poll(1, SECONDS);

        assertNotNull(message);
        assertEquals(content, message.getContent());
    }

    @Test
    void testStoredDataInRedis() throws InterruptedException {
        long from = 1l;
        long to = 2l;

        String content = "test content ";
        for (int i = 0; i < 30; i++) {
            Thread.sleep(200l);
            stompSession.send(SEND_ENDPOINT, new OneToOneChatMessageDTO(from, to, content + i));
        }

        OneToOneChatMessage message = blockingQueue.poll(1, SECONDS);

        assertNotNull(message);
        assertEquals(content + "0", message.getContent());

        List<OneToOneChatMessage> storedMessages = chatMessageRepository.findOneToOneMessagesByRoomIdWithPage(from, to, PageRequest.of(0, 20));

        assertEquals(20, storedMessages.size());
        assertEquals(content + "29", storedMessages.get(0).getContent());
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        return transports;
    }
}

