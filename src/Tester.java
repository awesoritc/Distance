import java.util.ArrayList;
import java.util.Random;

public class Tester {

    public static void main(String args[]) {



        Room r = new Room(1, 1, 1, 1, 0, "test");
        r.register_goods(1);
        r.register_goods(0);
        r.do_consume_room(3);
        r.do_consume_room(3);
        String a = String.valueOf(r.calc_suf_rate());
        System.out.println(a);

        /*ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            array.add(i);
        }


        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.get(i));
        }

        for (int i = 0; i < array.size(); i++) {
            if(array.get(i)%2 == 0){
                array.remove(i);
            }
        }


        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.get(i));
        }*/
    }

}
