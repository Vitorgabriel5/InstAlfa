package tinterPJ.demo.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tinterPJ.demo.messaging.events.MatchNotificationEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchNotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.main}")
    private String exchange;

    public void sendMacthNotification(MatchNotificationEvent event) {
        log.info("Publicando notificacao de match: {}", event.getMatchId());
        rabbitTemplate.convertAndSend(exchange, "match.nofication", event);
    }
}
