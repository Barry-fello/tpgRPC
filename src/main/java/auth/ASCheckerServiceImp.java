package auth;




import ditinn.proto.auth.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import journalisation.LogUtils;

import java.util.Objects;
import java.util.logging.Logger;

public class ASCheckerServiceImp extends ASCheckerGrpc.ASCheckerImplBase implements ServerInterceptor {
    //private static final Logger logger = Logger.getLogger(ASManagerServiceImp.class.getName());
    private final MetierAuth metierAuth;
    private final LogUtils logUtils;
    private int port;
    private String host;

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
            responseObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.GOOD).build());
        }else{
            responseObserver.onNext(IdentiteResponse.newBuilder().setStatus(ResponseStatus.BAD).build());
        }
        responseObserver.onCompleted();
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
                + ", Port=" + logUtils.getPort());
        logUtils.logToServer("Requête interceptée venant du ClientChecker", headers);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<>(call) {}, headers);
    }

}
