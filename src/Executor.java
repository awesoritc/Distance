import java.io.*;
import java.util.ArrayList;

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

        try {
            File file_static = new File(TYPE_STATIC + ".csv");
            if (file_static.exists()) {
                new FileWriter(file_static).write("");
            }
            File file_dynamic = new File(TYPE_DYNAMIC + ".csv");
            if (file_dynamic.exists()) {
                new FileWriter(file_dynamic).write("");
            }
            File file_dynamic_history = new File("history_dynamic.csv");
            if (file_dynamic_history.exists()) {
                new FileWriter(file_dynamic_history).write("");
            }
            File file_static_history = new File("history_static.csv");
            if (file_static_history.exists()) {
                new FileWriter(file_static_history).write("");
            }
        } catch (IOException e) {
            e.printStackTrace();
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



        //静的の方を出力
        try{
            File file = new File("history_static" + ".csv");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

            //pw.write("roomid,day,sales,shortage,consume\n");

            for (int i = 0; i < rooms1.length; i++) {
                ArrayList<Integer> sales_array = rooms1[i].getGoods_list().get(0).getSales_record();
                ArrayList<Integer> shortage_array = rooms1[i].getGoods_list().get(0).getShortage_record();
                ArrayList<Integer> consume_array = rooms1[i].getGoods_list().get(0).getConsume_history();
                for (int j = 0; j < sales_array.size(); j++) {



                    pw.write("roomID:" + String.valueOf(i) + ", day:" + String.valueOf(j) + ", sales:" +
                            String.valueOf(sales_array.get(j)) + ", shortage" + String.valueOf(shortage_array.get(j)) +
                            ", consume:" + String.valueOf(consume_array.get(j)) + "\n");

                    /*pw.write( String.valueOf(i) + "," + String.valueOf(j) + "," +
                            String.valueOf(sales_array.get(j)) + "," + String.valueOf(shortage_array.get(j)) +
                            "," + String.valueOf(consume_array.get(j)) + "\n");*/

                }
                pw.write("\n");
            }

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        //動的の方を出力
        try{
            File file = new File("history_dynamic" + ".csv");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

            //pw.write("roomid,day,sales,shortage,consume\n");

            for (int i = 0; i < rooms2.length; i++) {
                ArrayList<Integer> sales_array = rooms2[i].getGoods_list().get(0).getSales_record();
                ArrayList<Integer> shortage_array = rooms2[i].getGoods_list().get(0).getShortage_record();
                ArrayList<Integer> consume_array = rooms2[i].getGoods_list().get(0).getConsume_history();
                for (int j = 0; j < sales_array.size(); j++) {



                    pw.write("roomID:" + String.valueOf(i) + ", day:" + String.valueOf(j) + ", sales:" +
                                String.valueOf(sales_array.get(j)) + ", shortage" + String.valueOf(shortage_array.get(j)) +
                                ", consume:" + String.valueOf(consume_array.get(j)) + "\n");

                    /*pw.write( String.valueOf(i) + "," + String.valueOf(j) + "," +
                            String.valueOf(sales_array.get(j)) + "," + String.valueOf(shortage_array.get(j)) +
                            "," + String.valueOf(consume_array.get(j)) + "\n");*/

                }
                pw.write("\n");
            }

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
