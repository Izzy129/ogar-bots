package OgarBots;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
		/*
		 * cigar sends these when joining
		 * const SEND_254 = new Uint8Array([254, 6, 0, 0, 0]);
		 * const SEND_255 = new Uint8Array([255, 1, 0, 0, 0]);
		 */
		// send spawn request
		sendPlay("testing"); // name dont work, just sends chinese character

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
		// multiogar sends a binary buffer (0x14) for clearOwnedCells
		// we can use this as an indicator for bot death?
		ByteBuffer deathBuffer = ByteBuffer.allocate(3) //3 sounds enough lol
		deathBuffer.put((byte) 0x14);
		if (deathBuffer.equals(buffer)) {
			System.out.println("[BotClient] Received clearAllOwned packet");
		} 
		// this should only be used for raw debugging


		// System.out.println("Received buffer RAW: " +
		// Arrays.toString(buffer.array())); // or .array()?
		// System.out.println("Received buffer STRING: " + buffer.toString());
	}

	public void sendPlay(String name) {
		// future note: unescape doesnt do much in java, dw about it
		// refer to setStringUTF8(); 
		// in cigar's binaryWriter.js
		byte[] nameBytes = name.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(1 + nameBytes.length);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(0, (byte) 0);
		for (int i = 1; i < name.length(); i++) {
			buffer.put(i, (byte) name.charAt(i));
		}
		send(buffer.array());
		isAlive = true;
	}

	public void sendMouse(int x, int y) { // works

		ByteBuffer buffer = ByteBuffer.allocate(13); // allocate 13 bytes for mouse packet
		buffer.order(ByteOrder.LITTLE_ENDIAN); // cigar uses this for mouse packets, so I will too :troll:
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