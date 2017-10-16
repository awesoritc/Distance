import java.util.Random;

public class Executor {

    public static void main(String args[]) {


        Setting setting = new Setting();


        Room[] rooms = new Room[setting.number_of_rooms];
        int a = 0;
        for(int i = 0; i < setting.number_of_areas; i++){

            for(int j = 0; j < ((int)(setting.number_of_rooms/setting.number_of_areas)); j++){
                rooms[a] = new Room(i, j, setting, a);
                a++;
            }
        }

        //0が決め打ち, 1が動的
        Simulator simulator1 = new Simulator(setting, rooms, 0);
        Simulator simulator2 = new Simulator(setting, rooms, 1);

        //Simulator simulator = new Simulator(setting);

        for(int i = 0; i < setting.getDays(); i++){


            int area = i%5;

            simulator1.create_route(area, i);
            simulator1.do_consume(i);
            simulator1.do_replenishment(i);
            simulator2.create_route(area, i);
            simulator2.do_consume(i);
            simulator2.do_replenishment(i);

        }

        //System.out.println(simulator.counter);
        /*System.out.println(simulator.get_sales());
        System.out.println(simulator.get_shortage());

        System.out.println("");
        System.out.println(simulator.getTotal_time());
        System.out.println(simulator.counter);*/

        System.out.println(simulator1.getTotal_time());
        System.out.println(simulator2.getTotal_time());
        System.out.println();
        System.out.println(simulator1.get_sales());
        System.out.println(simulator2.get_sales());
        System.out.println();
        System.out.println(simulator1.get_shortage());
        System.out.println(simulator2.get_shortage());
    }
}
