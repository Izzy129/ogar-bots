package OgarBots;

public class MovementPacket {
    private int x;
    private int y;

    // constructors
    public MovementPacket() {
    }

    public MovementPacket(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    // accessors
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // mutators
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
