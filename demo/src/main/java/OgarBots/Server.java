package OgarBots;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class Server { // this file is for user info from userscript

    public static void start() throws InterruptedException {
        Configuration config = new Configuration();
        config.setHostname("localhost"); 
        config.setPort(8080); // port to listen on

        System.out.println("Server open for userscript connection on port");
        System.out.println("Server Hostname: " + config.getHostname());
        System.out.println("Server Port: " + config.getPort());

        final SocketIOServer server = new SocketIOServer(config);

        // add on start listener
        // userscript will send a message to this server when it starts
        // finish this later 



    }

}
