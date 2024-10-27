
package com.restaurant.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe Chef représente un acteur principal dans le système de restaurant.
 * Il est responsable de la réception des commandes des serveurs et de la
 * distribution
 * des tâches aux cuisiniers. Il peut également enregistrer de nouveaux
 * cuisiniers et serveurs.
 * 
 * <p>
 * Cette classe étend {@link AbstractActor} et utilise le modèle d'acteur d'Akka
 * pour
 * gérer les messages reçus.
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
     * Handles Order, RegisterCook, and RegisterWaiter messages.
     *
     * @return Receive object defining the message handling behavior.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    log.info("Le Chef a reçu une commande du serveur: {}", order);
                    // Distribute order to cooks
                })
                .match(RegisterCook.class, cook -> {
                    cooks.add(cook.cook);
                    log.info("Cook registered: {}", cook.cook);
                })
                .match(RegisterWaiter.class, waiter -> {
                    waiters.add(waiter.waiter);
                    log.info("Waiter registered: {}", waiter.waiter);
                })
                .build();
    }

    /**
     * Represents an order with a specific dish.
     */
    static public class Order {
        public final String dish;

        /**
         * Constructs an Order with the specified dish.
         *
         * @param dish The name of the dish.
         */
        public Order(String dish) {
            this.dish = dish;
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
}