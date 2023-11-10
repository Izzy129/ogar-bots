package OgarBots;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class BotClient extends WebSocketClient {
	public static int botAmount = 0;
	public final int botId;

	public boolean isAlive;
	public int sendTimeout = 500; // 500 ms
	public int respawnTime = 5000;

	public Map<String, String> httpHeaders = new HashMap<String, String>();

	public BotClient(URI serverURI, Map<String, String> httpHeaders) {
		super(serverURI, httpHeaders);
		botId = ++botAmount;
		isAlive = false;
	}

	@Override
	public void send(byte[] bytes) {
		if (isOpen()) { // send packet if connected
			super.send(bytes);
		} else {
			try { // reconnect, wait timeout, and try again
				System.out.println(
						"[BotClient] Bot " + this.botId + " not connected! Reconnecting in " + sendTimeout + " ms...");
				super.reconnect();
				Thread.sleep(500);
				this.send(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("[BotClient] Bot " + botId + " connected");

		// got these from badplayer55's onCellcraft
		send(new byte[] {
				(byte) 254, 5, 0, 0, 0
		});
		send(new byte[] {
				(byte) 255, 50, (byte) 137, 112, 79
		});
		// send initial spawn request
		sendPlay("life.");

		Timer spawnCheck = new Timer();
		spawnCheck.schedule(new TimerTask() {
			/*
			 * again, don't really know what this does, just got from badplayer's client
			 * I'm assuming it's checking if this bot can spawn again?
			 */
			@Override
			public void run() {
				send(new byte[] { 90, 51, 24, 34, (byte) 131 });
			}
		}, 0, 1750);
		Timer respawn = new Timer();
		respawn.schedule(new TimerTask() {
			/*
			 * if the "previous check" passes, then a new bot will be sent (since it died)
			 */
			@Override
			public void run() {
				sendPlay("death.");
			}
		}, 0, respawnTime);
		Timer mouseEvent = new Timer();
		mouseEvent.schedule(new TimerTask() {
			@Override
			public void run() {
				sendMouse(App.mouseX, App.mouseY);
			}

		}, 0, 1);
		Timer checkEvent = new Timer();
		checkEvent.schedule(new TimerTask() {
			@Override
			public void run() {
				if (App.doEject) {
					sendEject();
				}
				if (App.doSplit) {
					sendSplit();
				}
			}
		}, 0, 50);

	}

	public void sendSplit() {
		try {
			send(new byte[] {
					(byte) 17 // opcode from cigar
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendEject() {
		try {
			send(new byte[] {
					(byte) 21 // opcode from cigar
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		switch (code) {
			case 1000:
				System.out.println("[BotClient] Bot " + botId + " was kicked; Reason: " + reason);
				break;
			case 1006:
				System.out.println("[BotClient] Server shut down");
				break;
			default:
				System.out.println("[BotClient] Unknown disconnect, code: " + code);
				System.out.println("[BotClient] Reason: " + reason);
				break;
		}
	}

	@Override
	public void onMessage(String message) {
		System.out.println("[BotClient] received message: " + message);
	}

	@Override
	public void onMessage(ByteBuffer buffer) {

		// System.out.println("Received buffer RAW: " +
		// Arrays.toString(buffer.array())); // or .array()?
		// System.out.println("Received buffer STRING: " + buffer.toString());
	}

	public void sendPlay(String name) {
		/*
		 * name packet is a byte array with each character index surrounded by zeros
		 * ex: sendPlay("test");
		 * -> packet sent: {0,116,0,101,0,115,0,116,0}
		 * got idea from another client, and this worked with multiogarII so we're
		 * sticking to this
		 */
		char[] charArr = name.toCharArray();
		byte[] namePacket = new byte[charArr.length * 2 + 1];
		for (int i = 0; i < charArr.length; i++) {
			namePacket[i * 2] = (byte) 0;
			namePacket[i * 2 + 1] = (byte) charArr[i];
		}
		namePacket[namePacket.length - 1] = (byte) 0; // trailing 0
		send(namePacket);
	}

	public void sendMouse(int x, int y) {

		ByteBuffer buffer = ByteBuffer.allocate(13); // allocate 13 bytes for mouse packet
		buffer.order(ByteOrder.LITTLE_ENDIAN); // cigar uses this for mouse packets, so I will too 
		buffer.put(0, (byte) 16); // first byte used to indicate what type of packet this is to server
		buffer.putInt(1, x); // putInt() puts 4 bytes, so we need to put 4 bytes at a time
		buffer.putInt(5, y); // last putInt() used 4 bytes, so start now at 5 (1+4)
		buffer.putInt(9, 0); // last putInt() used 4 bytes, so start now at 9 (5+4)
		send(buffer.array());
	}

	@Override
	public void onError(Exception ex) {
		System.err.println("[BotClient] an error occurred:" + ex);
	}
}