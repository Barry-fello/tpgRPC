package auth;

import ditinn.proto.auth.ASCheckerGrpc;
import ditinn.proto.auth.ClientInfo;
import ditinn.proto.auth.Identite;
import ditinn.proto.auth.IdentiteResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.time.Instant;
import java.util.Scanner;


public class ClientChecker {
    public static void main(String[] args) {
        // Configuration du canal gRPC
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 28414)
                .usePlaintext() // Pas de chiffrement pour ce test
                .build();

        // Création du stub pour communiquer avec le serveur
        ASCheckerGrpc.ASCheckerBlockingStub stub = ASCheckerGrpc.newBlockingStub(channel);
        //try (Scanner scanner = new Scanner(System.in)) permet de fermer automatiquement le scanner
        try (Scanner scanner = new Scanner(System.in)) {
            while (true){
            // Récupération des informations utilisateur
                System.out.println("Entrez un login (Ou Quitter pour vous déconnecter ): ");
                String login = scanner.nextLine();

                if(login.equalsIgnoreCase("Quitter")){
                    System.out.println("Deconnexion  .");
                    break;
                }

                System.out.println("Entrez un password : ");
                String password = scanner.nextLine();

                // Construction de la requête
                Identite request = Identite.newBuilder()
                        .setLogin(login)                       // Champ "login" défini dans Identite
                        .setPassword(password)                 // Champ "password" défini dans Identite
                        .setTimestamp(Instant.now().toString()) // Champ "timestamp" défini dans Identite
                        .build();

                // Envoi de la requête au serveur
                IdentiteResponse response = stub.simpleCheck(request);

    //            // Affichage de la réponse
                System.out.println("Statut : " + response.getStatus());
                //System.out.println("Message : " + response.getMessage());
            }
        } catch (Exception e) {
            // Gestion des erreurs éventuelles
            System.err.println("Une erreur s'est produite : " + e.getMessage());
        }

    }
}

