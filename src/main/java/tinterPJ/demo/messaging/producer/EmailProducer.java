package tinterPJ.demo.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tinterPJ.demo.messaging.events.EmailEvent;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.main}")
    private String exchange;

    public void sendEmail(EmailEvent event) {
        log.info("Publicando email para: {}", event.getDestinatario());
        rabbitTemplate.convertAndSend(exchange, "email.send", event);
    }

    public void sendWelcomeEmail(String email, String nome) {
        EmailEvent event = EmailEvent.builder()
                .destinatario(email)
                .assunto("Bem-vindo ao MatchApp!")
                .template("Welcome")
                .dados(Map.of("nome", nome))
                .build();
        sendEmail(event);
    }

    public void sendMatchEmail(String email, String nome, String matchNome) {
        EmailEvent event = EmailEvent.builder()
                .destinatario(email)
                .assunto("Novo Match")
                .template("match")
                .dados(Map.of("nome", nome, "matchNome", matchNome))
                .build();
        sendEmail(event);
    }
}
