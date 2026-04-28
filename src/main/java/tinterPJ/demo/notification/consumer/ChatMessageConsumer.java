//package tinterPJ.demo.notification.consumer;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import tinterPJ.demo.messaging.events.ChatMessageEvent;
//import tinterPJ.demo.model.ChatMessage;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ChatMessageConsumer {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @RabbitListener(queues = "${rabbitmq.queue.chat}")
//    public void consumeChatMessage(ChatMessageEvent event) {
//        log.info("Consumindo mensagem de chat: {}", event.getMessageId());
//
//        try{
//            messagingTemplate.convertAndSendToUser(
//                    event.getDestinatarioId().toString(),
//                    "queue/messages",
//                    event
//            );
//
//            log.info("Mensagem eviada via WebSocket para userId: {}", event.getDestinatarioId());
//        } catch (Exception e){
//            log.error("Erro ao processar mensagem de chat: {}", e.getMessage());
//        }
//    }
//}
