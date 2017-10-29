import java.util.ArrayList;
import java.util.Random;

public class Tester {

    public static void main(String args[]) {


        Room r = new Room(4,0,0,4,0,"dynamic");

        for (int i = 0; i < 5; i++) {
            r.do_consume_room(i);
            r.replenishment_room(i);
        }
    }

}
