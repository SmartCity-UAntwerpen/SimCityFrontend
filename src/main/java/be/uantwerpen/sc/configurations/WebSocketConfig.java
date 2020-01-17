package be.uantwerpen.sc.configurations;

import be.uantwerpen.sc.Messages.CustomMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.List;

/**
 * Added By Andreas on 16/12/2019
 * This class configures the websocket for communication with the workers
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer
{
    //enables brokers and set connection point for the /topic and /queue at the endpoints
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config)
    {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/SimCity");
    }

    //Add endpoints to which the workers can connect
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry)
    {
        registry.addEndpoint("/Robot", "/Drone", "/F1","/worker").setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy())).setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/droneworker").setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy())).setAllowedOrigins("*");
    }

    /*@Override
    public boolean configureMessageConverters(List<MessageConverter> arg0) {

        CustomMessageConverter WorkerMessageConvertor = new CustomMessageConverter();
        arg0.add(WorkerMessageConvertor);
        return true;
    }*/

}
