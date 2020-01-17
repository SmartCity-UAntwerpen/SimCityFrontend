package be.uantwerpen.sc.Messages;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

public class CustomMessageConverter implements MessageConverter {
        @Override
        public Object fromMessage(Message<?> message, Class<?> targetClass) {
            Object payLoad = message.getPayload();
            //String temp2 = payLoad.toString();
           // WorkerMessage temp = (WorkerMessage)payLoad;
            //write your logic here.
            return payLoad;
        }

        @Override
        public Message<?> toMessage(Object payload, MessageHeaders headers) {
            // write your logic.
            return MessageBuilder.withPayload(payload).copyHeaders(headers).build();
        }
    }

