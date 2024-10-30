package com.restaurant.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * La classe Waiter représente un serveur dans le système de restaurant.
 * Il reçoit les commandes des clients, les transmet au Chef, récupère les plats préparés, et les renvoie aux clients.
 */
public class Waiter extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef chef;

    /**
     * Constructeur de la classe Waiter, recevant une référence de l'acteur Chef.
     * @param chef L'acteur chef à qui transmettre les commandes.
     */
    public Waiter(ActorRef chef) {
        this.chef = chef;
    }

    /**
     * Props pour créer une instance de l'acteur Waiter.
     * @param chef Référence de l'acteur chef
     * @return Props pour créer une instance de Waiter
     */
    public static Props props(ActorRef chef) {
        return Props.create(Waiter.class, () -> new Waiter(chef));
    }

    /**
     *
     * @return
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // Prendre une commande d'un client et la transmettre au Chef
                .match(String.class, dish -> {
                    log.info("Le serveur a reçu une commande pour: {}", dish);
                    chef.tell(new Chef.Order(dish), getSelf());
                })
                // Recevoir le plat préparé du chef
                .match(Chef.DishPrepared.class, dishPrepared -> {
                    log.info("Le serveur a récupéré le plat préparé: {} par {}", dishPrepared.dish, dishPrepared.cook);
                    // Logique pour "transmettre" le plat au client
                    // Par exemple : simulate serving to client
                    log.info("Le plat {} a été servi au client.", dishPrepared.dish);
                })
                .build();
    }
}
