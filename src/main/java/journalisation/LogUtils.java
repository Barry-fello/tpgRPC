package journalisation;
/*
 * Auteur : BARRY Ibrahima
 */
import ditinn.proto.auth.ClientInfo;
import ditinn.proto.auth.LoggingServiceGrpc;
import io.grpc.Metadata;
;

import java.util.logging.Logger;

public class LogUtils {
    public static final Logger logger = Logger.getLogger(LogUtils.class.getName());
    private final LoggingServiceGrpc.LoggingServiceBlockingStub logClient;

    private String host; // contient l'adresse IP du client
    private int port; // contient le port du client

    public LogUtils(LoggingServiceGrpc.LoggingServiceBlockingStub logClient) {
        this.logClient = logClient;
    }
    /** Méthode permettant de récupérer l'adresse IP et le port du client
     * @param inetSocketString : adresse IP et port du client sous la forme /IP:port
     */
    public void getRemoteAddr(String inetSocketString) {
        this.host = inetSocketString.substring(0, inetSocketString.lastIndexOf(':'));
        this.port = Integer.parseInt(inetSocketString.substring(inetSocketString
                .lastIndexOf(':') + 1));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void logToServer(String message, Metadata headers) {
        if (logClient == null) {
            // Journalisation locale si le serveur de journalisation est indisponible
            logger.warning("[Journalisation locale] " + message + " - Headers: " + headers);
            return;
        }

        StringBuilder headersString = new StringBuilder();
        for (String key : headers.keys()) {
            String value = headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
            if (value != null) {
                headersString.append("\n- ").append(key).append(": ").append(value);
            }
        }

        ClientInfo logRequest = ClientInfo.newBuilder()
                .setIpAddress(host)
                .setPort(port)
                .setMessage(message + "\n Headers:" + headersString)
                .build();
        try {
            logClient.simpleLog(logRequest);
        } catch (Exception e) {
            logger.info("Serveur de journalisation non connecté: " + e.getMessage());
        }
    }
}
