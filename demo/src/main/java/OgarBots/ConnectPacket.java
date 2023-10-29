package OgarBots;

public class ConnectPacket {
    private String ip;
    private String origin;

    // private String message;
    // private int x;
    // private int y;
    public ConnectPacket() {
        // empty constructor
    }

    public ConnectPacket(String ip, String origin) { // might need to add username?
        super();
        this.ip = ip;
        this.origin = origin;

        // this.message = message;
        // this.x = x;
        // this.y = y;
    }

    // accessors
    // public String getMessage() {
    //     return message;
    // }

    // public int getX() {
    //     return x;
    // }

    // public int getY() {
    //     return y;
    // }
    public String getIp() {
            return ip;
        }

    public String getOrigin() {
        return origin;
    }

    
    // setters
    // public void setMessage(String message) {
    //     this.message = message;
    // }

    // public void setX(int x) {
    //     this.x = x;
    // }

    // public void setY(int y) {
    //     this.y = y;
    // }
    public void setIp(String ip) {
            this.ip = ip;
        }
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    
}
