package tinterPJ.demo.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.ChatMessageDTO;
import tinterPJ.demo.messaging.events.ChatMessageEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageProducer {


    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.main}")
    private String exchange;

    public void sendMessage(ChatMessageEvent event) {
        log.info("Publicando mensagem de chat: {}", event.getMessageId());
        rabbitTemplate.convertAndSend(exchange, "chat.message", event);
    }
}
