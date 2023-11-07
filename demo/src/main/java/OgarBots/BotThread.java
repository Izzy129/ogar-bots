package OgarBots;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotThread extends Thread {
    Map<String, String> httpHeaders = new HashMap<String, String>();
    BotClient client = null;

    @Override
    public void run() {
        // fake headers to make server think a browser is connecting
        httpHeaders.put("Accept-Encoding", "gzip, deflate");
        httpHeaders.put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        httpHeaders.put("Cache-Control", "no-cache");
        httpHeaders.put("Connection", "Upgrade");
        httpHeaders.put("Sec-WebSocket-Version", "13");
        httpHeaders.put("Pragma", "no-cache");
        httpHeaders.put("Host", getHost(App.targetIp));
        httpHeaders.put("Origin", App.origin);
        httpHeaders.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        try {
            client = new BotClient(new URI(App.targetIp), httpHeaders);
            client.connect();
            System.out.println("[BotThread] Bot " + client.botId + " spawned");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public static String getHost(String ip) {

        Pattern pattern = Pattern.compile("ws:\\/\\/([^:]+)");
        Matcher matcher = pattern.matcher(ip);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "localhost";
        }
    }

}
