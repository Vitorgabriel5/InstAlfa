//package tinterPJ.demo.notification.consumer;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//import tinterPJ.demo.messaging.events.ImageProcessingEvent;
//
//import java.awt.*;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ImageProcessingConsumer {
//
//    @RabbitListener(queues = "${rabbitmq.queue.image}")
//    public void consumeImageProcessing(ImageProcessingEvent event) {
//        log.info("Consumindo processamento de imagem para userId: {}", event.getUserId());
//
//        try {
//            log.info("Redimensionando imagem: {}", event.getImagePath());
//
//            log.info("Comprimindo imagem...");
//
//            log.info("Fazendo upload para cloud storage...");
//
//            String thumbnailUrl = "" +event.getImagePath();
//            String mediumUrl = "" +event.getImagePath();
//            String largeUrl = "" +event.getImagePath();
//
//            log.info("Atualizando URLs no Banco de dados...");
//
//            log.info("Processamento de imagem concluido!");
//
//        }catch (Exception e){
//            log.error("Erro ao processar imagem: {}", e.getMessage());
//        }
//    }
//}
