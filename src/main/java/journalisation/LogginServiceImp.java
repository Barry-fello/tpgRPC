package journalisation;

import ditinn.proto.auth.ClientInfo;
import ditinn.proto.auth.EmptyResponse;
import ditinn.proto.auth.LoggingServiceGrpc;
import io.grpc.stub.StreamObserver;

public class LogginServiceImp extends LoggingServiceGrpc.LoggingServiceImplBase {
    @Override
    public void simpleLog(ClientInfo request, StreamObserver<EmptyResponse> responseObserver) {
        System.out.println("Journalisation reçue :");
        System.out.println("IP : " + request.getIpAddress());
        System.out.println("Port : " + request.getPort());
        System.out.println("Message : " + request.getMessage());

        responseObserver.onNext(EmptyResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
