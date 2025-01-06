package journalisation;

/*
 * Auteur : BARRY Ibrahima
 */
import io.grpc.Server;
import io.grpc.ServerBuilder;


public class GestLog {
    public static void main(String[] args) {
        try {
            // Création et démarrage du serveur gRPC
            Server server = ServerBuilder
                    .forPort(3244)
                    .addService(new LogginServiceImp())
                    .build()
                    .start();
            System.out.println("Serveur de journalisation démarré sur le port 3244");

            // Arrêt propre du serveur lors de l'interruption
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Arrêt du serveur de journalisation");
                if (server != null) {
                    server.shutdown();
                }
            }));

            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
