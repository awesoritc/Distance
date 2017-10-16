import java.util.Random;

public class Executor {

    public static void main(String args[]) {


        Setting setting = new Setting();

        Simulator simulator = new Simulator(setting);

        for(int i = 0; i < setting.getDays(); i++){


            int area = i%5;

            simulator.create_route(area, i);
            simulator.do_consume(i);
            //simulator.do_replenishment(area, i);
            simulator.do_replenishment(i);

        }

        System.out.println(simulator.counter);
        //simulator.do_consume();
        System.out.println(simulator.get_sales());
        System.out.println(simulator.get_shortage());

    }
}
