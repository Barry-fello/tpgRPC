package auth;




import ditinn.proto.auth.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Objects;
import java.util.logging.Logger;

public class ASCheckerServiceImp extends ASCheckerGrpc.ASCheckerImplBase implements ServerInterceptor {
    private static final Logger logger = Logger.getLogger(ASManagerServiceImp.class.getName());
    private final MetierAuth metierAuth;
    private final LoggingServiceGrpc.LoggingServiceBlockingStub logClient;
    private int port;
    private String host;

    public ASCheckerServiceImp(MetierAuth metierAuth, LoggingServiceGrpc.LoggingServiceBlockingStub logClient) {
        this.metierAuth = metierAuth;
        this.logClient = logClient;
    }
    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void simpleCheck(Identite request, StreamObserver<IdentiteResponse> responseObserver) {
        if(metierAuth.tester(request.getLogin(), request.getPassword())){
            responseObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.GOOD).build());
        }else{
            responseObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.BAD).build());
        }
        responseObserver.onCompleted();
    }
    private void getRemoteAddr(String inetSocketString) {
        host = inetSocketString.substring(0, inetSocketString.lastIndexOf(':'));
        port = Integer.parseInt(inetSocketString.substring(inetSocketString.lastIndexOf(':') + 1));
    }

    private void logToServer(String message) {
        // Construire le message de journalisation
        ClientInfo logRequest = ClientInfo.newBuilder()
                .setIpAddress(host)
                .setPort(port)
                .setMessage(message)
                .build();

        // Envoyer au serveur de journalisation
        logClient.simpleLog(logRequest);
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        // Obtenir l'adresse du client
        String inetSocketString = Objects.requireNonNull(call.getAttributes()
                .get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)).toString();
        getRemoteAddr(inetSocketString);

        // Journaliser la connexion interceptée
        logger.info("Le client connecté: IP=" + host + ", Port=" + port);
        logToServer("Requête interceptée venant du ClientChecker");

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<>(call) {}, headers);
    }

}
