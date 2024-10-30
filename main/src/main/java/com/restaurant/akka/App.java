package com.restaurant.akka;

import akka.actor.ActorSystem;
import akka.actor.ActorRef;

/**
 * Hello world!
 *
 */


public class App {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("RestaurantSystem");

        ActorRef chefActorRef = system.actorOf(Chef.props(), "chef");

        ActorRef waiterActorRef = system.actorOf(Waiter.props(chefActorRef), "waiter");

        chefActorRef.tell(new Chef.RegisterWaiter(waiterActorRef), ActorRef.noSender());


        waiterActorRef.tell("Pasta", ActorRef.noSender());

        // Fin du programme
        system.terminate();

    }
}