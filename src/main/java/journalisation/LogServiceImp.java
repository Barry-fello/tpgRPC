package journalisation;

import ditinn.proto.auth.GetLogRequest;
import ditinn.proto.auth.LogRequest;
import ditinn.proto.auth.LogServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;

public class LogServiceImp extends LogServiceGrpc.LogServiceImplBase {
    private final ArrayList<LogRequest> logRequests = new ArrayList<>();


    // Implémentation de la méthode getLogs avec filtrage par critère 'filter'
    @Override
    public void getLogs(GetLogRequest getLogRequest, StreamObserver<LogRequest> streamObserver) {
        String filter = getLogRequest.getFilter();

        // Si un filtre est spécifié, on filtre les logs en fonction de ce critère
        List<LogRequest> filteredLogs = logRequests;
        if (!filter.isEmpty()) {
            filteredLogs = logRequests.stream()
                    .filter(log -> log.getAction().contains(filter) || log.getDetails().contains(filter))
                    .toList();
        }

        // Envoi des logs filtrés au client
        filteredLogs.forEach(streamObserver::onNext);
        streamObserver.onCompleted();
    }

    // Méthode pour ajouter un log dans la liste
    public void addLog(LogRequest log) {
        if (log != null) {
            logRequests.add(log);
        }
    }
}
