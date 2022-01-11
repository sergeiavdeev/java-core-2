package lesson1;

public class RunningTrack implements Crossable{

    private final int length;

    public RunningTrack(int length) {
        this.length = length;
    }

    @Override
    public void cross(Athletics athlete) {
        athlete.run(length);
    }
}
