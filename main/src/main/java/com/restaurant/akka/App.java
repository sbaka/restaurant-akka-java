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

        ActorRef waiter = system.actorOf(Waiter.props(chef), "waiter");
        ActorRef client = system.actorOf(Client.props(waiter), "client");

        //chef.tell(new Chef.RegisterWaiter(waiter), ActorRef.noSender());

        //client.tell(new Client.StartOrder(), ActorRef.noSender());
    }
}