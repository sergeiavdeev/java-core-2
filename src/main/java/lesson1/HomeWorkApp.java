package lesson1;

public class HomeWorkApp {

    public static void main(String[] args) {

        Athletics[] athletes = {
                new Human(700, 150),
                new Cat(100, 200),
                new Bot(500, 0),
                new Cat(120, 210),
                new Human(1500, 120)
        };

        Crossable[] equipments = {
                new Barrier(50),
                new RunningTrack(500),
                new Barrier(70)
        };

        for (Athletics athlete : athletes) {
            for (Crossable equipment: equipments) {
                equipment.cross(athlete);
            }
        }
    }
}
