package OgarBots;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;

public class Server { // this file is for user info from userscript

    public static void start() throws InterruptedException {
        Configuration config = new Configuration();
        config.setHostname("localhost"); 
        config.setPort(8080); // port to listen on

        System.out.println("[Server] Socket.IO Server starting at port " + config.getPort() + "...");
        

        final SocketIOServer server = new SocketIOServer(config);
        
        server.addConnectListener(new ConnectListener() { // when user connects
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("User connected with ip: " + client.getRemoteAddress());
            }
        });

        server.addEventListener("start", ConnectPacket.class, new DataListener<ConnectPacket>() {
            @Override
            public void onData(SocketIOClient client, ConnectPacket packet, AckRequest ackSender) throws Exception {
                String ip = packet.getIp();
                String origin = packet.getOrigin();
                System.out.println("[Server] User requested bots");
                System.out.println("[Server] IP: " + ip);
                System.out.println("[Server] Origin: " + origin);
                App.targetIp = ip;
                App.origin = origin;
                App.startBots();
                System.out.println("[Server] Bots started!");
            }
        });
        
        server.addEventListener("movement", MovementPacket.class, new DataListener<MovementPacket>() {
            @Override
            public void onData(SocketIOClient client, MovementPacket data, AckRequest ackSender) throws Exception {
                int x = data.getX();
                int y = data.getY();
                App.moveBots(x, y);
            }
        });
        server.addEventListener("eject", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
                App.ejectBots();
            }
        });
        server.addEventListener("split", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
                App.splitBots();
            }
        });

        // once multi threading is added, please just handle constant emits from here
        // for now, we'll just get userscript to request every 2 seconds and return it back
        server.addEventListener("requestCount", Integer.class, new DataListener<Integer>() {

            @Override
            public void onData(SocketIOClient client, Integer data, AckRequest ackSender) throws Exception {
                server.getBroadcastOperations().sendEvent("botCount", App.botsConnected);
            }
            
        });
 
        server.start();
        System.out.println("[Server] Socket.IO Server started at port " + config.getPort() + "!");
        System.out.println();
    }

}
