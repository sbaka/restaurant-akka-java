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
    private final String[] dishes =  {"Pasta", "Pizza", "Salad", "Burger", "Soup"};
    private final Random random = new Random();

    public Client(ActorRef waiter) {
        this.waiter = waiter;
    }

    /**
     * Méthode statique pour initialiser l'acteur Client.
     * @param waiter Le serveur associé au client.
     * @return Props pour créer un acteur Client.
     */
    public static Props props(ActorRef waiter) {
        return Props.create(Client.class, () -> new Client(waiter));
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
                    log.info("Le client {} passe une commande pour: {}", getSelf().path().name() ,dish);
                    // le client passe la commande au serveur, le serveur transmettra la commande au chef
                    waiter.tell(new Waiter.Order(dish, getSelf()), getSelf());
                })
                .match(DishServed.class, dishServed -> {
                    log.info("Le client {} a reçu son plat: {}", getSelf().path().name(), dishServed.dish);
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
