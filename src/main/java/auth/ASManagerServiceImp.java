package auth;
/*
 * Auteur : BARRY Ibrahima
 */
import ditinn.proto.auth.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import journalisation.LogUtils;

import java.util.Objects;


public class ASManagerServiceImp extends ASManagerGrpc.ASManagerImplBase implements ServerInterceptor {

    private final MetierAuth metierAuth;
    private final ASCheckerServiceImp asCheckerServiceImp;
    private final LogUtils logUtils;
    private int port;
    private String host;

    public ASManagerServiceImp(MetierAuth metierAuth, LogUtils logUtils) {
        this.metierAuth = metierAuth;
        this.asCheckerServiceImp = new ASCheckerServiceImp(metierAuth, logUtils);
        this.logUtils = logUtils;
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
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        // Obtenir l'adresse du client
        String inetSocketString = Objects.requireNonNull(call.getAttributes()
                .get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)).toString();
        logUtils.getRemoteAddr(inetSocketString);

        // Journaliser la connexion interceptée
        LogUtils.logger.info("Le client connecté: IP=" + logUtils.getHost()
                + ", Port=" + logUtils.getPort()
                + ", resultat" + headers);
        logUtils.logToServer("Requête interceptée venant du  ClientManager",headers);

        return next.startCall(
                new ForwardingServerCall.SimpleForwardingServerCall<>(call) {}, headers);
    }

}
