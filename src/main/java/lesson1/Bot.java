package lesson1;

public class Bot implements Athletics{

    private final int maxLength;
    private final int maxHeight;
    boolean isActive;

    public Bot(int maxLength, int maxHeight) {
        this.maxLength = maxLength;
        this.maxHeight = maxHeight;
        this.isActive = true;
    }

    @Override
    public void run(int length) {
        if (isActive) {
            System.out.printf("Bot %s run %d\n", maxLength < length ? "not" : "", length);
        }
        if (maxLength < length)isActive = false;
    }

    @Override
    public void jump(int height) {
        if (isActive) {
            System.out.printf("Bot %s jump %d\n", maxHeight < height ? "not" : "", height);
        }
        if (maxHeight < height)isActive = false;
    }
}
