package tinterPJ.demo.messaging.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;


@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.chat}")
    private String chatQueue;

    @Value("${rabbitmq.queue.match}")
    private String matchQueue;

    @Value("${rabbitmq.queue.email}")
    private String emailQueue;

    @Value("${rabbitmq.queue.image}")
    private String imageQueue;

    @Value("${rabbitmq.exchange.main}")
    private String exchange;

    @Bean
    public Queue chatQueue(){
        return new Queue(chatQueue, true);
    }

    @Bean
    public Queue matchQueue(){
        return new Queue(matchQueue, true);
    }

    @Bean
    public Queue emailQueue(){
        return new Queue(emailQueue, true);
    }

    @Bean
    public Queue imageQueue(){
        return new Queue(imageQueue, true);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange exchange){
        return BindingBuilder
                .bind(chatQueue)
                .to(exchange)
                .with("chat.*");
    }

    @Bean
    public Binding matchBinding(Queue matchQueue, TopicExchange exchange){
        return BindingBuilder
                .bind(matchQueue)
                .to(exchange)
                .with("match.*");
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange exchange){
        return BindingBuilder
                .bind(emailQueue)
                .to(exchange)
                .with("email.*");
    }

    @Bean
    public Binding imageBinding(Queue imageQueue, TopicExchange exchange){
        return BindingBuilder
                .bind(imageQueue)
                .to(exchange)
                .with("image.*");
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}