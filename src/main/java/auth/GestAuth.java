package auth;

import ditinn.proto.auth.LoggingServiceGrpc;
import io.grpc.*;
import journalisation.LogUtils;

public class GestAuth {

    public static void main(String[] args) {
        try {
            System.err.println("Serveur Auth RUNNING");
            MetierAuth metierAuth = new MetierAuth();

            ManagedChannel logChannel = ManagedChannelBuilder.forAddress("localhost", 3244)
                    .usePlaintext()
                    .build();
            LoggingServiceGrpc.LoggingServiceBlockingStub logStub = LoggingServiceGrpc.newBlockingStub(logChannel);

            LogUtils logClient = new LogUtils(logStub);
            // Instanciation du servant Gestionnaire
            ASCheckerServiceImp checkerService = new ASCheckerServiceImp(metierAuth, logClient);
            ASManagerServiceImp managerService = new ASManagerServiceImp(metierAuth, logClient);
            // Initialisation du serveur sur le port 40555
            Server server = ServerBuilder.forPort(28414)
                    .addService(ServerInterceptors.intercept(checkerService, checkerService))
                    .addService(ServerInterceptors.intercept(managerService, managerService))
                    .build()
                    .start();
            // Interception Ctrl C et arrêt processus
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (server != null) {
                    server.shutdown();
                }
            }));
            // Boucle infinie
            server.awaitTermination();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }
}
