import java.util.ArrayList;
import java.util.Random;

public class Tester {

    public static void main(String args[]) {


        ArrayList<Integer> array = new ArrayList<>();
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
        }
    }

}
