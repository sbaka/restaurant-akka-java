# Projet de Restaurant avec Akka

Ce projet est une application Java qui utilise Akka pour la gestion des acteurs dans un système de restaurant. La classe principale `Chef` est responsable de la gestion des commandes et de l'enregistrement des cuisiniers et des serveurs.

## Structure du Projet

Le projet est structuré comme suit :

- **src/main/java** : Contient le code source Java.

  - **actors** : Contient les classes d'acteurs comme `Chef`, `Cook`, et `Waiter`.
  - **messages** : Contient les classes de messages échangés entre les acteurs.
  - **App.java** : Point d'entrée principal de l'application.

- **src/test/java** : Contient les tests unitaires pour les classes d'acteurs et de messages.

- **pom.xml** : Fichier de configuration Maven pour la gestion des dépendances et la compilation du projet.

## Requis

- Java 8 ou supérieur
- Maven 3.6.0 ou supérieur

## Installation

1. Cloner le dépôt :

   ```sh
   git clone <URL_DU_DEPOT>
   cd <NOM_DU_DEPOT>
   ```

2. Compiler le projet avec Maven :

   ```sh
   mvn clean install
   ```

## Utilisation

### Initialisation du Système d'Acteurs

Pour initialiser le système d'acteurs et créer une instance de l'acteur `Chef` :

```java
import akka.actor.ActorSystem;
import akka.actor.ActorRef;

public class App {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("RestaurantSystem");
        ActorRef chef = system.actorOf(Chef.props(), "chef");
    }
}
```

### Enregistrer un Cuisinier

Pour enregistrer un nouveau cuisinier :

```java
ActorRef cookActorRef = system.actorOf(Cook.props(), "cook");
chef.tell(new Chef.RegisterCook(cookActorRef), ActorRef.noSender());
```

### Enregistrer un Serveur

Pour enregistrer un nouveau serveur :

```java
ActorRef waiterActorRef = system.actorOf(Waiter.props(), "waiter");
chef.tell(new Chef.RegisterWaiter(waiterActorRef), ActorRef.noSender());
```

### Envoyer une Commande

Pour envoyer une commande au chef :

```java
chef.tell(new Chef.Order("Pasta"), waiterActorRef);
```

## Classes et Messages

### Classe `Chef`

La classe `Chef` représente un acteur principal dans le système de restaurant. Elle est responsable de la réception des commandes des serveurs et de la distribution des tâches aux cuisiniers. Elle peut également enregistrer de nouveaux cuisiniers et serveurs.

#### Messages

- `Order`: Représente une commande passée par un serveur.
- `RegisterCook`: Représente l'enregistrement d'un nouveau cuisinier.
- `RegisterWaiter`: Représente l'enregistrement d'un nouveau serveur.