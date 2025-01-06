package auth;
/*
 * Auteur : BARRY Ibrahima
 */
import ditinn.proto.auth.LoggingServiceGrpc;
import io.grpc.*;
import journalisation.LogUtils;

public class GestAuth {

    public static void main(String[] args) {
        try {
            System.err.println("Serveur Auth RUNNING");
            MetierAuth metierAuth = new MetierAuth();

            // Tentative de connexion au serveur de journalisation
            LogUtils logClient = null;
            ManagedChannel logChannel = null;
            try {
                logChannel = ManagedChannelBuilder.forAddress("localhost", 3244)
                        .usePlaintext()
                        .build();
                LoggingServiceGrpc.LoggingServiceBlockingStub logStub = LoggingServiceGrpc.newBlockingStub(logChannel);
                logClient = new LogUtils(logStub);
            } catch (Exception e) {
                System.err.println("Serveur de journalisation indisponible. Le système utilisera une journalisation locale.");
                logClient = new LogUtils(null);  // Mode dégradé sans journalisation distante
            }

            // Instanciation des services
            ASCheckerServiceImp checkerService = new ASCheckerServiceImp(metierAuth, logClient);
            ASManagerServiceImp managerService = new ASManagerServiceImp(metierAuth, logClient);

            // Initialisation du serveur sur le port 28414
            Server server = ServerBuilder.forPort(28414)
                    .addService(ServerInterceptors.intercept(checkerService, checkerService))
                    .addService(ServerInterceptors.intercept(managerService, managerService))
                    .build()
                    .start();

            ManagedChannel finalLogChannel = logChannel;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (server != null) {
                    server.shutdown();
                }
                if (finalLogChannel != null) {
                    finalLogChannel.shutdown();
                }
            }));

            server.awaitTermination();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

}
