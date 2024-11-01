package com.restaurant.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La classe Chef représente un acteur principal dans le système de restaurant.
 * Il est responsable de la réception des commandes des serveurs et de la
 * distribution des tâches aux cuisiniers. Il peut également enregistrer de
 * nouveaux
 * cuisiniers et serveurs.
 * 
 * <p>
 * Cette classe étend {@link AbstractActor} et utilise le modèle d'acteur d'Akka
 * pour gérer les messages reçus.
 * </p>
 * 
 * <p>
 * Les messages que cet acteur peut recevoir incluent :
 * </p>
 * <ul>
 * <li>{@link Order} : Représente une commande passée par un serveur.</li>
 * <li>{@link RegisterCook} : Représente l'enregistrement d'un nouveau
 * cuisinier.</li>
 * <li>{@link RegisterWaiter} : Représente l'enregistrement d'un nouveau
 * serveur.</li>
 * <li>{@link DishPrepared} : Représente un plat préparé par un cuisinier.</li>
 * </ul>
 * 
 * <p>
 * Exemple d'utilisation :
 * </p>
 * 
 * <pre>
 * {@code
 * ActorSystem system = ActorSystem.create("RestaurantSystem");
 * ActorRef chef = system.actorOf(Chef.props(), "chef");
 * 
 * // Enregistrer un cuisinier
 * chef.tell(new Chef.RegisterCook(cookActorRef), ActorRef.noSender());
 * 
 * // Enregistrer un serveur
 * chef.tell(new Chef.RegisterWaiter(waiterActorRef), ActorRef.noSender());
 * 
 * // Envoyer une commande
 * chef.tell(new Chef.Order("Pasta"), waiterActorRef);
 * }
 * </pre>
 * 
 * @see AbstractActor
 * @see Props
 */
public class Chef extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final List<ActorRef> cooks = new ArrayList<>();
    private final List<ActorRef> waiters = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Creates and returns Props for creating a Chef actor.
     *
     * @return Props for creating a Chef actor.
     */
    static public Props props() {
        return Props.create(Chef.class, () -> new Chef());
    }

    /**
     * Creates the receive behavior for the Chef actor.
     * Handles Order, RegisterCook, RegisterWaiter, and DishPrepared messages.
     *
     * @return Receive object defining the message handling behavior.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    log.info("Le Chef a reçu une commande du serveur: {}", order.waiter.path().name());
                    log.info("Le Chef a reçu une commande de : {}", order.dish);
                    if (!cooks.isEmpty()) {
                        // Distribute order to the first available cook
                        ActorRef cook = cooks.get(random.nextInt(cooks.size()));
                        log.info("Commande distribuée au cuisinier: {}", cook.path().name());
                        cook.tell(new Cook.PrepareDish(order.dish, order.waiter, order.client), getSelf());
                    } else {
                        log.warning("Aucun cuisinier disponible pour la commande: {}", order.dish);
                    }
                })
                .match(RegisterCook.class, cook -> {
                    cooks.add(cook.cook);
                    log.info("Cuisinier enregistré: {}", cook.cook.path().name());
                })
                .match(RegisterWaiter.class, waiter -> {
                    waiters.add(waiter.waiter);
                    log.info("Serveur enregistré: {}", waiter.waiter.path().name());
                })
                .match(DishPrepared.class, dishPrepared -> {
                    log.info("Plat préparé: {} par {}", dishPrepared.dish, dishPrepared.cookName);
                    // Send the prepared dish back to the waiter
                    dishPrepared.waiter.tell(dishPrepared, getSelf());
                })
                .build();
    }

    /**
     * Represents an order with a specific dish.
     */
    static public class Order {
        public final String dish;
        public final ActorRef waiter;
        public final ActorRef client;

        /**
         * Constructs an Order with the specified dish.
         *
         * @param dish The name of the dish.
         */
        public Order(String dish, ActorRef waiter, ActorRef client) {
            this.dish = dish;
            this.waiter = waiter;
            this.client = client;
        }
    }

    /**
     * Represents a message to register a cook.
     */
    static public class RegisterCook {
        public final ActorRef cook;

        /**
         * Constructs a RegisterCook message with the specified cook actor reference.
         *
         * @param cook The actor reference of the cook to be registered.
         */
        public RegisterCook(ActorRef cook) {
            this.cook = cook;
        }
    }

    /**
     * Represents a message to register a waiter.
     */
    static public class RegisterWaiter {
        public final ActorRef waiter;

        /**
         * Constructs a RegisterWaiter message with the specified waiter actor
         * reference.
         *
         * @param waiter The actor reference of the waiter to be registered.
         */
        public RegisterWaiter(ActorRef waiter) {
            this.waiter = waiter;
        }
    }

    /**
     * Represents a message indicating a dish has been prepared.
     */
    static public class DishPrepared {
        public final String dish;
        public final String cookName;
        public final ActorRef waiter;
        public final ActorRef client;

        /**
         * Constructs a DishPrepared message with the specified dish, cook, and waiter.
         *
         * @param dish     The name of the dish.
         * @param cookName The name of the cook who prepared the dish.
         * @param waiter   The actor reference of the waiter who will serve the dish.
         */
        public DishPrepared(String dish, String cookName, ActorRef waiter, ActorRef client) {
            this.dish = dish;
            this.cookName = cookName;
            this.waiter = waiter;
            this.client = client;
        }
    }
}