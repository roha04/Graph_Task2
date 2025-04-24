package com.example.graphqlsubscriptions.controller;

import com.example.graphqlsubscriptions.model.Message;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Controller
public class MessageController {

    // створюємо Sinks.Many для розсилки повідомлень
    private final Sinks.Many<Message> messageSink =
            Sinks.many().multicast().onBackpressureBuffer();

    @MutationMapping
    public Message createMessage(@Argument String content) {
        Message message = new Message(UUID.randomUUID().toString(), content);
        // емісія події в Sink
        messageSink.tryEmitNext(message);
        return message;
    }

    @SubscriptionMapping
    public Flux<Message> messageAdded() {
        // підписка на поток подій
        return messageSink.asFlux();
    }
}