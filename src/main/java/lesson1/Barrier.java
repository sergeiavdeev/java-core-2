package lesson1;

public class Barrier implements Crossable{

    private final int height;

    public Barrier(int height) {
        this.height = height;
    }

    @Override
    public void cross(Athletics athlete) {
        athlete.jump(height);
    }
}
