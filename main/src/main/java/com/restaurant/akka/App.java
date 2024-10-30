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

        ActorRef cook1 = system.actorOf(Cook.props("Cook 1"), "Cook1");
        ActorRef cook2 = system.actorOf(Cook.props("Cook 2"), "Cook2");


        ActorRef waiter = system.actorOf(Waiter.props(chef), "waiter");
        ActorRef client = system.actorOf(Client.props(waiter), "client");


        chef.tell(new Chef.RegisterWaiter(waiter), ActorRef.noSender());

        chef.tell(new Chef.RegisterCook(cook1), ActorRef.noSender());
        chef.tell(new Chef.RegisterCook(cook2), ActorRef.noSender());

        client.tell(new Client.StartOrder(), ActorRef.noSender());
        client.tell(new Client.StartOrder(), ActorRef.noSender());
        client.tell(new Client.StartOrder(), ActorRef.noSender());
    }
}