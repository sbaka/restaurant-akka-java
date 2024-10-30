package com.restaurant.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Cook extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String name;

    public Cook(String name) {
        this.name = name;
    }

    public static Props props(String name) {
        return Props.create(Cook.class, () -> new Cook(name));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PrepareDish.class, prepareDish -> {
                    log.info("Le cuisinier {} prépare le plat: {}", name, prepareDish.dish);
                    Thread.sleep(1000); // Simulation d'un délai de préparation
                    getSender().tell(new Chef.DishPrepared(prepareDish.dish, name, prepareDish.waiter), getSelf());
                })
                .build();
    }

    public static class PrepareDish {
        public final String dish;
        public final ActorRef waiter;

        public PrepareDish(String dish, ActorRef waiter) {
            this.dish = dish;
            this.waiter = waiter;
        }
    }
}