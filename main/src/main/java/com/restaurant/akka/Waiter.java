package com.restaurant.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
/**
 * La classe Waiter représente un acteur responsable de recevoir les commandes des clients
 * et de les transmettre au Chef. Elle reçoit également les plats préparés du Chef
 * pour les livrer au client.
 *
 * <p> Le Waiter doit être enregistré auprès du Chef en envoyant un message {@link Chef.RegisterWaiter}
 * avant de pouvoir transmettre les commandes. Cette étape permet au Chef de reconnaître
 * le Waiter comme un acteur valide pour l’envoi des commandes. </p>
 *
 * <p>
 * Les messages traités par cet acteur incluent :
 * </p>
 * <ul>
 * <li>{@link Chef.Order} : une nouvelle commande de la part du client.</li>
 * <li>{@link Chef.DishPrepared} : Indique qu’un plat est prêt à être servi au client.</li>
 * </ul>
 *
 * <p> Exemple d'utilisation dans une application principale :</p>
 * <pre>
 * {@code
 * ActorSystem system = ActorSystem.create("RestaurantSystem");
 * ActorRef chef = system.actorOf(Chef.props(), "chef");
 * ActorRef waiter = system.actorOf(Waiter.props(chef), "waiter");
 * ActorRef client = system.actorOf(Client.props(waiter), "client");
 *
 * // Enregistrer le serveur auprès du chef
 * chef.tell(new Chef.RegisterWaiter(waiter), ActorRef.noSender());
 *
 * // Le client initie une commande
 * client.tell(new Client.StartOrder(), ActorRef.noSender());
 * }
 * </pre>
 *
 * @see AbstractActor
 * @see Props
 */
public class Waiter extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef chef;

    /**
     * Constructeur de la classe {@code Waiter}.
     * Initialise le serveur avec une référence de l'acteur {@code Chef} pour transmettre les commandes.
     *
     * @param chef L'acteur Chef à qui transmettre les commandes.
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
     * Définit les comportements de réception de messages pour le serveur {@code Waiter}.
     * <ul>
     *     <li>Lorsqu'il reçoit un message de type {@code Chef.Order} : il transmet la commande au {@code Chef}.</li>
     *     <li>Lorsqu'il reçoit un message de type {@code Chef.DishPrepared} : il transmet le plat préparé au client.</li>
     * </ul>
     *
     * @return {@code Receive} définissant les comportements du serveur.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // Prendre une commande d'un client et la transmettre au Chef
                .match(Chef.Order.class, order -> {
                    log.info("Le serveur a reçu une commande pour: {}", order.dish);
                    chef.tell(new Chef.Order(order.dish), getSelf());
                    //chef.tell(new Chef.Order(order.dish, order.client), getSelf());
                })
                // Recevoir le plat préparé du chef
                .match(Chef.DishPrepared.class, dishPrepared -> {
                    log.info("Le serveur a récupéré le plat préparé: {} par {}", dishPrepared.dish, dishPrepared.cook);
                    // Transmettre le plat au client
                    //dishPrepared.client.tell(new Client.DishServed(dishPrepared.dish), getSelf());
                    log.info("Le plat {} a été servi au client.", dishPrepared.dish);

                    })
                .build();
    }
}
