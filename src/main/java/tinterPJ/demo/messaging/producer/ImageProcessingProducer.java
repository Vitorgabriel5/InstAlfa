//package tinterPJ.demo.messaging.producer;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import tinterPJ.demo.messaging.events.ImageProcessingEvent;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ImageProcessingProducer {
//
//    private final RabbitTemplate rabbitTemplate;
//
//    @Value("${rabbitmq.exchange.main}")
//    private String exchange;
//
//    public void processImage(ImageProcessingEvent event) {
//        log.info("Publicando processamento de imagem para userId: {}", event.getUserId());
//        rabbitTemplate.convertAndSend(exchange, "image.process",  event);
//    }
//}
