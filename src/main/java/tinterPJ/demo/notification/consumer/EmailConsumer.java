//package tinterPJ.demo.notification.consumer;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//import tinterPJ.demo.messaging.events.EmailEvent;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class EmailConsumer {
//
//    @RabbitListener(queues ="${rabbitmq.queue.email}")
//    public void consumeEmail(EmailEvent event) {
//        log.info("Consumindo email para: {}", event.getDestinatario());
//
//        try {
//            log.info("Enviando email...");
//            log.info("Para: {}",event.getDestinatario());
//            log.info("Assunto: {}", event.getAssunto());
//            log.info("Template: {}", event.getTemplate());
//            log.info("Dados: {}", event.getDados());
//
//            log.info("Email envido com sucesso!");
//        } catch (Exception e) {
//            log.error("Erro ao enviar email: {}", e.getMessage());
//        }
//    }
//}
