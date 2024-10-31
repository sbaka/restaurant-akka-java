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
        ActorRef client1 = system.actorOf(Client.props(waiter, "Client 1"), "client-1");
        ActorRef client2 = system.actorOf(Client.props(waiter, "Client 2"), "client-2");
        ActorRef client3 = system.actorOf(Client.props(waiter, "Client 3"), "client-3");

        chef.tell(new Chef.RegisterWaiter(waiter), ActorRef.noSender());

        chef.tell(new Chef.RegisterCook(cook1), ActorRef.noSender());
        chef.tell(new Chef.RegisterCook(cook2), ActorRef.noSender());

        client1.tell(new Client.StartOrder(), ActorRef.noSender());
        client2.tell(new Client.StartOrder(), ActorRef.noSender());
        client3.tell(new Client.StartOrder(), ActorRef.noSender());
    }
}