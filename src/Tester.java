import java.util.ArrayList;
import java.util.Random;

public class Tester {

    public static void main(String args[]) {


        Setting setting = new Setting();

        ArrayList<Integer> array = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            Random rand = new Random();
            array.add(rand.nextInt(100));
        }

int tmp = 0;
        if(array.size() > setting.getMoving_average_interval()){
            for(int i = 0; i < setting.getMoving_average_interval(); i++){
                tmp += array.get(array.size()-(i+1));
            }
        }
        System.out.println(tmp);
        System.out.println();
        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.get(i));
        }
        /*Simulator simulator = new Simulator(setting);
        Room room = new Room(0, 0, setting, 0);
        room.register_goods(0);
        int[][] gravity_points = {{1,1},{1,14},{17, 18},{12,12},{4,15}};
        room.setDistance_to_gravity(gravity_points);

        for (int i = 0; i < 100; i++) {
            room.do_consume();
            System.out.println("stock:" + room.getGoods_list().get(0).getStock());

            System.out.println("shortage:" + room.get_room_shortage_til_next(i%5));

            if(i%5 == 0){
                room.replenishment_all();
                System.out.println("rep");
            }
        }

        for (int i = 0; i < room.getGoods_list().get(0).getSales_record().size(); i++) {
            System.out.println("day:" + i + ", sales:" + room.getGoods_list().get(0).getSales_record().get(i));
        }*/
    }







    private static void goods_test(){

        Setting setting = new Setting(true);

        int room_area_num = 0;

        Goods goods = new Goods(0, setting);
        for (int i = 0; i < 20; i++) {
            System.out.println("day : " + i);
            int[] consume = goods.consume();
            System.out.println("shortage : " + consume[0] + ", sales : " + consume[1] +
                    ", stock : " + goods.getStock() +
                    ", expect : " + goods.get_shortage_til_next(Util.get_interval(i%5, room_area_num)));
            if(i%5 == room_area_num){
                goods.replenishment();
                System.out.println("rep : " + goods.getStock());
            }
            System.out.println("");
        }
    }



    private static void room_setDistance_test(){
        Setting setting = new Setting(true);

        int room_area_num = 0;
        int route_num = 0;

        int[][] a = {{1,4}, {3,3}, {10,0}, {0,10}, {100, 100}};

        Room room = new Room(room_area_num, route_num, setting, 0);

        int[] room_pos = {room.getX_pos(), room.getY_pos()};
        System.out.println("(" + room_pos[0] + "," + room_pos[1] + ")");
        room.setDistance_to_gravity(a);

        for (int i = 0; i < 5; i++) {
            System.out.println(room.getDistance_to_gravity()[i]);
        }
    }


    private static void room_createPosition_test(){
        Setting setting = new Setting(true);


        int route_num = 0;

        int[][] a = {{1,4}, {3,3}, {10,0}, {0,10}, {100, 100}};

        for (int i = 0; i < 100; i++) {
            int room_area_num = i%5;
            Room room = new Room(room_area_num, route_num, setting, 0);
            int x = room.getX_pos();
            int y = room.getY_pos();
            switch(room_area_num){
                case 0:
                    if(0 <= x && x < setting.getX_bottom_divider() && 0 <= y && y < setting.getY_divider()){
                        System.out.println("ok");
                    }else{
                        System.out.println("ng");
                    }
                    break;
                case 1:
                    if(setting.getX_bottom_divider() <= x && x < 20 && 0 <= y && y < setting.getY_divider()){
                        System.out.println("ok");
                    }else{
                        System.out.println("ng");
                    }
                    break;
                case 2:
                    if(setting.getX_top_divider()[1] <= x && x < 20 && setting.getY_divider() <= y && y < 20){
                        System.out.println("ok");
                    }else{
                        System.out.println("ng");
                    }
                    break;
                case 3:
                    if(setting.getX_top_divider()[0] <= x && x < setting.getX_top_divider()[1] && setting.getY_divider() <= y && y < 20){
                        System.out.println("ok");
                    }else{
                        System.out.println("ng");
                    }
                    break;
                case 4:
                    if(0 <= x && x < setting.getX_top_divider()[0] && setting.getY_divider() <= y && y < 20){
                        System.out.println("ok");
                    }else{
                        System.out.println("ng");
                    }
                    break;
            }
        }

    }


    private static void room_doConsume_test(){
        Setting setting = new Setting(true);

        int room_area_num = 0;
        int route_num = 0;

        Room room = new Room(room_area_num, route_num, setting, 0);

        room.register_goods(0);
        room.register_goods(1);
        for (int i = 0; i < 20; i++) {
            int[] test = room.do_consume();

            System.out.println("sales : " + test[1] + ", shortage : " + test[0]);
            System.out.println("");
        }

    }


    private static void room_replenishmentAll_test(){
        Setting setting = new Setting();

        int room_area_num = 0;
        int route_num = 0;


        for (int a = 0; a < 100; a++) {
            Room room = new Room(room_area_num, route_num, setting, 0);

            room.register_goods(0);
            room.register_goods(2);
            room.register_goods(1);
            room.register_goods(0);

            room.do_consume();
            room.do_consume();
            room.do_consume();
            room.do_consume();
            for (int i = 0; i < room.getGoods_list().size(); i++) {
                //System.out.println(room.getGoods_list().get(i).getStock());
            }

            boolean flag = false;
            for (int i = 0; i < room.getGoods_list().size(); i++) {
                if(room.getGoods_list().get(i).getStock() != setting.getMax_item()){
                    flag = true;
                }else{
                    flag = false;
                    break;
                }
            }


            room.replenishment_all();

            for (int i = 0; i < room.getGoods_list().size(); i++) {
                if(room.getGoods_list().get(i).getStock() == setting.getMax_item() && flag){
                    System.out.println("ok");
                }else{
                    System.out.println("ng");
                }
            }
        }
    }


    private static void room_getShortageTilNext_test(){
        Setting setting = new Setting(true);

        int room_area_num = 0;
        int route_num = 0;

        Room room = new Room(room_area_num, route_num, setting, 0);
        room.register_goods(0);

        for (int i = 0; i < 100; i++) {
            room.do_consume();
            if(i%5 == room_area_num) room.replenishment_all();
            room.get_room_shortage_til_next(Util.get_interval(i%5, room_area_num));
        }

        ArrayList<Integer> array = room.getGoods_list().get(0).getSales_record();
        System.out.println("");
        System.out.println("");
        System.out.println("");
        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.get(i));
        }


    }

}
