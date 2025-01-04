package journalisation;

import ditinn.proto.auth.ClientInfo;
import ditinn.proto.auth.LoggingServiceGrpc;
import io.grpc.Metadata;
;

import java.util.logging.Logger;

public class LogUtils {
    public static final Logger logger = Logger.getLogger(LogUtils.class.getName());
    private  final LoggingServiceGrpc.LoggingServiceBlockingStub logClient ;

    private String host;
    private int port;

    public LogUtils(LoggingServiceGrpc.LoggingServiceBlockingStub logClient) {
        this.logClient = logClient;
    }
    public void getRemoteAddr(String inetSocketString) {
        this.host = inetSocketString.substring(0, inetSocketString.lastIndexOf(':'));
        this.port = Integer.parseInt(inetSocketString.substring(inetSocketString.lastIndexOf(':') + 1));
    }
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
    public void logToServer(String message, Metadata headers) {
        // Convertir les métadonnées en une représentation lisible
        StringBuilder headersString = new StringBuilder();
        for (String key : headers.keys()) {
            String value = headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
            if (value != null) {
                headersString.append("\n- ").append(key).append(": ").append(value);
            }
        }

        // Construire le message de journalisation
        ClientInfo logRequest = ClientInfo.newBuilder()
                .setIpAddress(host)
                .setPort(port)
                .setMessage(message + "\n Headers:" + headersString)
                .build();

        // Envoyer au serveur de journalisation
        logClient.simpleLog(logRequest);
    }

}
