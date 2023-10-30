package OgarBots;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import io.netty.channel.unix.Buffer;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class BotClient extends WebSocketClient {
	public static int botAmount = 0;
	public final int botId;

	public boolean isAlive;
	public int sendTimeout = 500; // 500 ms

	
	public Map<String, String> httpHeaders = new HashMap<String, String>();

	public BotClient(URI serverURI, Map<String, String> httpHeaders) {
		super(serverURI, httpHeaders);
		botId = ++botAmount;
		isAlive=false;
	}
	@Override
	public void send(byte[] bytes) {
		if (isOpen()) { // send packet if connected
			super.send(bytes);
		} else {
			try { // wait 500 ms and try again
				super.reconnect();
				System.out.println("Bot " + this.botId + " not connected! Waiting " + sendTimeout +  " ms...");
				Thread.sleep(500); 
				this.send(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// System.out.println("Sent bytes: " + Arrays.toString(bytes));
	}
	

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Bot " + botId + " connected");

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
				System.out.println("Bot " + botId + " was kicked; Reason: " + reason);
				break;
			case 1006:
				System.out.println("Server shut down");
				break;
			default:
				System.out.println("Unknown disconnect, code: " + code);
				System.out.println("Reason: " + reason);
				break;
		}
	}

	@Override
	public void onMessage(String message) {
		System.out.println("received message: " + message);
	}
	@Override
	public void onMessage(ByteBuffer buffer) {
		// System.out.println("Received buffer RAW:  " + Arrays.toString(buffer.array())); // or .array()?
		// System.out.println("Received buffer STRING: " + buffer.toString()); 
	}


	public void sendPlay(String name) {
		// sooo apparently cigar unescapes this using encodeURIComponent, idk if you can
		// do this in java
		// also has their own method of setting strings or something, refer to
		// setStringUTF8();
		// in cigar's binaryWriter.js

		// for now ill just take weird chinese name lol
		// byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
		// // System.out.println("nameBytes: " + Arrays.toString(nameBytes));
		// ByteBuffer buffer = ByteBuffer.allocate(1 + nameBytes.length);
		// buffer.put((byte) 0x00);
		// buffer.put(nameBytes);
		// buffer.flip();
		// send(buffer);
		// isAlive = true;

		byte[] nameBytes = name.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(1 + nameBytes.length);
		// ByteBuffer buffer = ByteBuffer.allocate(1 + name.length());
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(0, (byte) 0);
		for (int i = 1; i < name.length(); i++) {
			buffer.put(i, (byte) name.charAt(i));
		}
		// buffer.putInt(9,0);
		send(buffer.array());


	}

	public void sendMouse(int x, int y) { // WORKS NOW YAY

		ByteBuffer buffer = ByteBuffer.allocate(13); // allocate 13 bytes for mouse packet
		buffer.order(ByteOrder.LITTLE_ENDIAN); // cigar uses this for mouse packets, so I will too :troll:
		buffer.put(0, (byte) 16); // first byte used for something
		buffer.putInt(1, x); // putInt() puts 4 bytes, so we need to put 4 bytes at a time
		buffer.putInt(5, y); // last putInt() used 4 bytes, so start now at 5
		buffer.putInt(9, 0);

		send(buffer.array());
	}

	@Override
	public void onError(Exception ex) {
		System.err.println("an error occurred:" + ex);
	}
}