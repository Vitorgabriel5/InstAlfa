package tinterPJ.demo.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tinterPJ.demo.messaging.events.MatchNotificationEvent;
import tinterPJ.demo.messaging.producer.EmailProducer;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchNotificationConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final EmailProducer emailProducer;

    @RabbitListener(queues = "${rabbitmq.queue.match}")
    public void consumeMatchNotification(MatchNotificationEvent event) {
        log.info("Consumindo notificacao de match: {}", event.getMatchId());

        try {
            Map<String, Object> matchData = new HashMap<>();
            matchData.put("matchId", event.getMatchId());
            matchData.put("message", "E um match!");

            matchData.put("userId", event.getUser2Id());
            matchData.put("userName", event.getUser2Nome());
            messagingTemplate.convertAndSend(
                    event.getUser1Id().toString(),
                    "/queue/match",
                    matchData
            );

            matchData.put("userId", event.getUser1Id());
            matchData.put("userName", event.getUser1Nome());
            messagingTemplate.convertAndSendToUser(
                    event.getUser2Id().toString(),
                    "/queue/match",
                    matchData
            );

            emailProducer.sendMatchEmail(
                    event.getUser1Id() + "@email.com",
                    event.getUser1Nome(),
                    event.getUser2Nome()
            );

            emailProducer.sendMatchEmail(
                    event.getUser2Id() +"@gmail.com",
                    event.getUser2Nome(),
                    event.getUser1Nome()
            );

            log.info("Notificacao de match enviado com sucesso!");

        }catch (Exception e){
            log.error("Erro ao processar notificacao de match: {}", e.getMessage());
        }
    }
}
