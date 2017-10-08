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

        /*for(int i = 0; i < 10; i++){
            simulator.create_route(0);
        }

        simulator.create_route(1);
        simulator.create_route(2);
        simulator.create_route(3);
        simulator.create_route(4);*/

        //simulator.do_consume();
        System.out.println(simulator.get_sales());
        System.out.println(simulator.get_shortage());

    }
}
