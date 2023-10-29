package OgarBots;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static String targetIp = ""; // from userscript
    public static String origin = ""; // from userscript
    

    public static int botCount = 40;
    public static int connectTimeout = 500;
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        System.out.println("Welcome to OgarBots");
        Server.start();
    }
    public static String getHost(String ip) throws URISyntaxException {

        Pattern pattern = Pattern.compile("ws:\\/\\/([^:]+)");
        Matcher matcher = pattern.matcher(ip);
        
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            return matcher.group(1);
        } else {
            return "localhost";
        }
    }
    public static void startBots() throws InterruptedException, URISyntaxException {
        Map<String, String> httpHeaders = new HashMap<String, String>();
        // fake headers to make server think a browser is connecting
        httpHeaders.put("User-Agent", "ImNotABot");
        httpHeaders.put("Accept-Encoding", "gzip, deflate");
		httpHeaders.put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
		httpHeaders.put("Cache-Control", "no-cache");
		httpHeaders.put("Connection", "Upgrade");
		httpHeaders.put("Sec-WebSocket-Version", "13");
		httpHeaders.put("Pragma", "no-cache");
        httpHeaders.put("Host", getHost(targetIp)); // grab from userscript later 
		httpHeaders.put("Connection", "Upgrade");
        httpHeaders.put("Origin", origin); // from userscript
		httpHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        ArrayList<BotClient> bots = new ArrayList<BotClient>();
        
        for (int i = 0; i < botCount; i++) {
            BotClient c = new BotClient(new URI(targetIp), httpHeaders);
            bots.add(c);
            c.connect();
            
            // sleep for .5 second
            try {
                Thread.sleep(connectTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
