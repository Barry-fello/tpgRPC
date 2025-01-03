package auth;

import ditinn.proto.auth.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Objects;
import java.util.logging.Logger;

public class ASManagerServiceImp extends ASManagerGrpc.ASManagerImplBase implements ServerInterceptor {
    private static final Logger logger = Logger.getLogger(ASManagerServiceImp.class.getName());

    private final MetierAuth metierAuth;
    private final ASCheckerServiceImp asCheckerServiceImp;
    private final LoggingServiceGrpc.LoggingServiceBlockingStub logClient;

    private int port;
    private String host;

    public ASManagerServiceImp(MetierAuth metierAuth, LoggingServiceGrpc.LoggingServiceBlockingStub logClient) {
        this.metierAuth = metierAuth;
        this.asCheckerServiceImp = new ASCheckerServiceImp(metierAuth,logClient);
        this.logClient = logClient;
    }

    @Override
    public void simplesCheck(Identite request, StreamObserver<IdentiteResponse> responseObserver) {
        asCheckerServiceImp.simpleCheck(request, responseObserver);
    }
    @Override
    public void add(AuthRequest authRequest, StreamObserver<IdentiteResponse> responseStreamObserver){
        if(metierAuth.creer(authRequest.getIdentiteRequest().getLogin(),authRequest.getIdentiteRequest().getPassword())){
            responseStreamObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.DONE) .build());
        }else {
            responseStreamObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.ERROR).build());
        }
        responseStreamObserver.onCompleted();
    }

    @Override
    public void delete(AuthRequest authRequest, StreamObserver<IdentiteResponse> responseStreamObserver){
        if(metierAuth.supprimer(authRequest.getIdentiteRequest().getLogin(), authRequest.getIdentiteRequest().getPassword())){
            responseStreamObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.DONE).build());
        }else{
            responseStreamObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.ERROR).build());
        }
        responseStreamObserver.onCompleted();
    }
    @Override
    public void update(AuthRequest authRequest, StreamObserver<IdentiteResponse> responseStreamObserver){
        if(metierAuth.mettreAJour(authRequest.getIdentiteRequest().getLogin(), authRequest.getIdentiteRequest().getPassword())){
            responseStreamObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.DONE).build());
        }else{
            responseStreamObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.ERROR).build());
        }
        responseStreamObserver.onCompleted();

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
        logToServer("Requête interceptée venant du  ClientManager");

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<>(call) {}, headers);
    }

}
