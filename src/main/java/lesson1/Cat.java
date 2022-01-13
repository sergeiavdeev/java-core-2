package lesson1;

public class Cat implements Athletics{

    private final int maxLength;
    private final int maxHeight;
    boolean isActive;

    public Cat(int maxLength, int maxHeight) {
        this.maxLength = maxLength;
        this.maxHeight = maxHeight;
        this.isActive = true;
    }

    @Override
    public void run(int length) {
        if (isActive) {
            System.out.printf("Cat %s run %d\n", maxLength < length ? "not" : "", length);
        }
        if (maxLength < length)isActive = false;
    }

    @Override
    public void jump(int height) {
        if (isActive) {
            System.out.printf("Cat %s jump %d\n", maxHeight < height ? "not" : "", height);
        }
        if (maxHeight < height)isActive = false;
    }
}
