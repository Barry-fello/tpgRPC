package auth;
/*
 * Auteur : BARRY Ibrahima
 */
import ditinn.proto.auth.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import journalisation.LogUtils;

import java.util.Objects;

public class ASCheckerServiceImp extends ASCheckerGrpc.ASCheckerImplBase implements ServerInterceptor {
    private final MetierAuth metierAuth;
    private final LogUtils logUtils;

    public ASCheckerServiceImp(MetierAuth metierAuth, LogUtils logUtils) {
        this.metierAuth = metierAuth;
        this.logUtils = logUtils;
    }
    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void simpleCheck(Identite request, StreamObserver<IdentiteResponse> responseObserver) {
        if(metierAuth.tester(request.getLogin(), request.getPassword())){
            responseObserver.onNext(IdentiteResponse.newBuilder()
                    .setStatus(ResponseStatus.GOOD).build());
        }else{
            responseObserver.onNext(IdentiteResponse
                    .newBuilder().setStatus(ResponseStatus.BAD).build());
        }
        responseObserver.onCompleted();
    }
    /**
     * Méthode permettant de récupérer l'adresse IP et le port du client lors d'un appel gRPC
     * @param call l'appel gRPC
     * @param requestHeaders les requestHeaders de la requête
     * @param next le prochain appel gRPC (c'est cela qui appellera la bonne méthode rcp)
     * @return un listener d'appel RPC
     * @param <ReqT> paramètre générique requête
     * @param <RespT> paramètre générique réponse
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next) {
        // Obtenir l'adresse du client
        String inetSocketString = Objects.requireNonNull(call.getAttributes()
                .get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)).toString();
        logUtils.getRemoteAddr(inetSocketString);

        // Journaliser la connexion interceptée
        LogUtils.logger.info("Le client connecté: IP=" + logUtils.getHost()
                + ", Port=" + logUtils.getPort());
        logUtils.logToServer("Requête interceptée venant du ClientChecker", requestHeaders);

        return next.startCall(new ForwardingServerCall
                .SimpleForwardingServerCall<>(call) {}, requestHeaders);
    }

}
