package journalisation;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ditinn.proto.auth.LogServiceGrpc;
import ditinn.proto.auth.GetLogRequest;
import ditinn.proto.auth.LogRequest;

import java.util.ArrayList;
import java.util.List;

public class ClientLog {
    private final LogServiceGrpc.LogServiceBlockingStub blockingStub;

    public ClientLog(ManagedChannel channel) {
        this.blockingStub = LogServiceGrpc.newBlockingStub(channel);
    }

    // Récupère les logs en streaming et les ajoute à la liste
    public List<LogRequest> getLogs() {
        GetLogRequest request = GetLogRequest.newBuilder().build();
        List<LogRequest> logList = new ArrayList<>();

        // Appel bloquant pour récupérer tous les logs
        blockingStub.getLogs(request).forEachRemaining(logList::add);

        // Si aucun log n'est récupéré, on affiche un message
        if (logList.isEmpty()) {
            System.out.println("Aucun log disponible.");
        }

        return logList;
    }

    public static void main(String[] args) {
        // Configuration du canal gRPC
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 3244)
                .usePlaintext() // Pas de chiffrement pour ce test
                .build();

        // Création du client
        ClientLog client = new ClientLog(channel);

        // Récupération automatique des logs dès la connexion
        try {
            List<LogRequest> logs = client.getLogs();

            // Si des logs ont été récupérés, on les affiche
            if (!logs.isEmpty()) {
                logs.forEach(log -> System.out.println("Log: " + log.getDetails()));
            }
        } catch (Exception e) {
            // Gestion des erreurs éventuelles
            System.err.println("Une erreur s'est produite : " + e.getMessage());
        } finally {
            // Fermeture du canal gRPC après récupération des logs
            channel.shutdown();
        }
    }
}