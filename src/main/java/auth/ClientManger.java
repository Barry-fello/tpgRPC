package auth;


import ditinn.proto.auth.ASManagerGrpc;


import ditinn.proto.auth.AuthRequest;
import ditinn.proto.auth.Identite;
import ditinn.proto.auth.IdentiteResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientManger {
    public static void main(String[] args) {
        ManagedChannel channel =  ManagedChannelBuilder.forAddress("localhost",28414)
                        .usePlaintext().build();
       ASManagerGrpc.ASManagerBlockingStub blockingStub = ASManagerGrpc.newBlockingStub(channel);



        Scanner scanner = new Scanner(System.in);
        boolean fin = false;
        while (!fin) {

            System.out.println("--- Menu ---");
            System.out.println("0. Quitter");
            System.out.println("1. verifier login");
            System.out.println("2. Ajouter login");
            System.out.println("3. modifier login");
            System.out.println("4. supprimer login");
            System.out.println("Faite votre choix: ");
            int choix = -1;

            try {
                choix = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(" Vous devez entrer un entier. Veuillez réessayer.");
                scanner.nextLine();
                continue;
            }
            switch (choix) {
                case 0 :
                    scanner.close();
                    System.out.println(" Aurevoir ");
                    fin = true;
                    break;
                case 1:
                    System.out.print("Entrer un login: ");
                    String login = scanner.nextLine();
                    System.out.print("Entrer un password: ");
                    String password = scanner.nextLine();

                    Identite checkRequest = Identite.newBuilder()
                            .setLogin(login)
                            .setPassword(password)
                            //.setTimestamp(String.valueOf(System.currentTimeMillis()))
                            .build();
                    IdentiteResponse identiteResponse = blockingStub.simplesCheck(checkRequest);
                    System.out.println("Status: " + identiteResponse.getStatus());
                    break;
                case 2:
                    System.out.print("Entrer un login: ");
                    login = scanner.nextLine();
                    System.out.print("Entrer un password: ");
                    password = scanner.nextLine();

                    Identite identite = Identite.newBuilder().setLogin(login).setPassword(password).build();
                    AuthRequest addRequest = AuthRequest.newBuilder().setIdentiteRequest(identite).build();
                    IdentiteResponse addResponse = blockingStub.add(addRequest);
                    System.out.println("Status: " + addResponse.getStatus());
                    break;
                case 3:
                    System.out.print("Entrer un login: ");
                    login = scanner.nextLine();
                    System.out.print(" Entrer un password: ");
                    password = scanner.nextLine();

                    Identite newIdentite = Identite.newBuilder().setLogin(login).setPassword(password).build();
                    AuthRequest updateRequest = AuthRequest.newBuilder().setIdentiteRequest(newIdentite).build();
                    IdentiteResponse updateResponse = blockingStub.update(updateRequest);
                    System.out.print(" Status: " + updateResponse);
                    break;

                case 4:
                    System.out.print("Entrer un login: ");
                    login = scanner.nextLine();
                    System.out.print(" Entrer un password: ");
                    password = scanner.nextLine();

                    Identite deleteIdentite = Identite.newBuilder().setLogin(login).setPassword(password).build();
                    AuthRequest deleteteRequest = AuthRequest.newBuilder().setIdentiteRequest(deleteIdentite).build();
                    IdentiteResponse deleteteResponse = blockingStub.delete(deleteteRequest);
                    System.out.print(" Status: " + deleteteResponse);
                    break;
                default:
                    System.out.print("Veuillez choisir entre les choix possible 0 à 4 ");
                    break;
            }
        }

    }

}