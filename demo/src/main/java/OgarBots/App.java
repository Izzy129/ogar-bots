package OgarBots;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    public static String targetIp = ""; // from userscript
    public static String origin = ""; // from userscript
    public static int botCount = 20;
    public static int connectTimeout = 0; // add later when multithreading and proxies used
    public static ArrayList<BotThread> bots = new ArrayList<BotThread>();
    public static int botsConnected = 0;

    public static boolean doSplit = false;
    public static boolean doEject = false;
    public static boolean doChatSpam = false;
    public static int mouseX = 0;
    public static int mouseY = 0;
    public static ExecutorService executor = Executors.newFixedThreadPool(botCount);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[App] Welcome to OgarBots");
        Server.start();
    }

    public static void startBots() throws InterruptedException {

        for (int i = 0; i < botCount; i++) {
            executor.execute(new BotThread());
            botsConnected++;
            try {
                Thread.sleep(connectTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // bots.add(current);
            // BotClient c = new BotClient(new URI(targetIp), httpHeaders);
            // bots.add(c);
            // c.connect();

            // sleep for connectTimeout

        }
        // while (true) {
        // for (BotClient b:bots) {
        // b.sendPlay("1234"); // name dont work, just sends chinese character

        // }
        // Thread.sleep(3000);
        // }
    }

    // for now, single threaded works fine but look into multithreading later
    // public static void moveBots(int x, int y) {
    // for (BotThread thread : bots) {
    // thread.client.sendMouse(x, y);
    // }
    // }

    // public static void ejectBots() {
    // for (BotThread thread : bots) {
    // thread.client.sendEject();
    // }
    // }

    // public static void splitBots() {
    // for (BotThread thread : bots) {
    // thread.client.sendSplit();
    // }
    // }

}
