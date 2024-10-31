package com.restaurant.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Random;

public class Client extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef waiter;
    private final String name;
    private final String[] dishes =  {"Pasta", "Pizza", "Salad", "Burger", "Soup"};
    private final Random random = new Random();

    public Client(ActorRef waiter, String name) {
        this.waiter = waiter;
        this.name = name;
    }

    public static Props props(ActorRef waiter, String name) {
        return Props.create(Client.class, () -> new Client(waiter, name));
    }

    /**
     * Comportement de réception des messages pour le client.
     * @return Receive définit les messages traités.
     */

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartOrder.class, start -> {
                    // Le client choisit un plat aléatoire et le commande au serveur
                    String dish = dishes[random.nextInt(dishes.length)];
                    log.info("Client {} passe une commande pour: {}", name, dish);
                    waiter.tell(new Chef.Order(dish), getSelf());
                })
                .match(DishServed.class, dishServed -> {
                    log.info("Client {} a reçu son plat: {}", name, dishServed.dish);
                })
                .build();
    }

    /**
     * Message pour déclencher une commande.
     */
    public static class StartOrder {}

    /**
     * Message pour indiquer qu'un plat est servi.
     */

    public static class DishServed {
        public final String dish;

        public DishServed(String dish) {
            this.dish = dish;
        }
    }

}
