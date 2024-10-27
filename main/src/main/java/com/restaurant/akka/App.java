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
        ActorRef chef = system.actorOf(Chef.props(), "chef");

        // TODO: ajouter les autres acteurs
    }
}