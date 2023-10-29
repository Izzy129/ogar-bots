package OgarBots;

public class ConnectPacket {
    private String ip;
    private String origin;

    // constructors
    public ConnectPacket() {
    }

    public ConnectPacket(String ip, String origin) {
        super();
        this.ip = ip;
        this.origin = origin;
    }

    // accessors
    public String getIp() {
        return ip;
    }

    public String getOrigin() {
        return origin;
    }

    // mutators
    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

}
