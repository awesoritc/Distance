import java.io.*;

public class Executor {

    public static void main(String args[]) {


        Setting setting = new Setting();
        String TYPE_STATIC = "static";
        String TYPE_DYNAMIC = "dynamic";

        String filename = "rooms1.csv";


        //TODO:これをそのまま両方に渡すのではなくて、ファイルから読み込んだ値を利用して2つのRoom配列を新規で別々に作る必要がある
        //ファイルの書き込み
        File rooms_file = new File(filename);
        if(!rooms_file.exists()){
            //ファイルがない場合作成
            //部屋番号、エリア番号、座標、登録する商品番号

            Util.create_room_file(filename);

        }


        //room_file読み込み
        int[][] room_element = Util.read_room_file(filename);
        System.out.println(room_element.length);
        for (int i = 0; i < room_element.length; i++) {
            System.out.println(room_element[i][0] + "," + room_element[i][1] + ",(" + room_element[i][2] + ":" + room_element[i][3] + ")," +room_element[i][4]);
        }


        Room[] rooms1 = new Room[setting.number_of_rooms];
        Room[] rooms2 = new Room[setting.number_of_rooms];

        for (int i = 0; i < rooms1.length; i++) {

            //room_element[roomId][0~4](部屋番号、エリア番号、x座標、y座標、登録する商品番号)
            rooms1[i] = new Room(room_element[i][0], room_element[i][1], room_element[i][2], room_element[i][3], + room_element[i][4], TYPE_STATIC);
            rooms2[i] = new Room(room_element[i][0], room_element[i][1], room_element[i][2], room_element[i][3], + room_element[i][4], TYPE_DYNAMIC);
        }

        //0が決め打ち, 1が動的
        Simulator simulator1 = new Simulator(setting, rooms1, 0, TYPE_STATIC);
        Simulator simulator2 = new Simulator(setting, rooms2, 1, TYPE_DYNAMIC);

        for(int i = 0; i < setting.getDays(); i++){


            int area = i%5;

            simulator1.create_route(area, i);
            simulator1.do_consume_simulator(i);
            simulator1.do_replenishment_simulator(i);
            simulator2.create_route(area, i);
            simulator2.do_consume_simulator(i);
            simulator2.do_replenishment_simulator(i);

        }

        System.out.println("time1:" + simulator1.getTotal_time());
        System.out.println("time2:" + simulator2.getTotal_time());
        System.out.println();
        System.out.println("sales1:" + simulator1.get_sales());
        System.out.println("sales2:" + simulator2.get_sales());
        System.out.println("shortage1:" + simulator1.get_shortage());
        System.out.println("shortage2:" + simulator2.get_shortage());
        System.out.println();
        System.out.println(simulator2.counterb);


    }
}
