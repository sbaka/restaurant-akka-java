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
        ActorRef chef = system.actorOf(Chef.props(), "Chef");

        ActorRef[] cooks = new ActorRef[6];
        for (int i = 0; i < 6; i++) {
            cooks[i] = system.actorOf(Cook.props("Cook_" + (i + 1)), "Cook" + (i + 1));
        }

        ActorRef[] waiters = new ActorRef[3];
        for (int i = 0; i < 3; i++) {
            waiters[i] = system.actorOf(Waiter.props(chef), "Waiter" + (i + 1));
        }

        for (ActorRef cook : cooks) {
            chef.tell(new Chef.RegisterCook(cook), ActorRef.noSender());
        }

        for (ActorRef waiter : waiters) {
            chef.tell(new Chef.RegisterWaiter(waiter), ActorRef.noSender());
        }

        Runnable clientOrders = () -> {
            for (int i = 0; i < 10; i++) {
                String clientName = "Client_" + (i + 1);
                ActorRef client = system.actorOf(Client.props(waiters[i % 3]), clientName);
                for (int j = 0; j < 3; j++) {
                    client.tell(new Client.StartOrder(), ActorRef.noSender());
                }
                System.out.println(clientName + " has placed 3 orders.");
            }
        };

        Thread clientThread = new Thread(clientOrders);
        clientThread.start();
    }
}