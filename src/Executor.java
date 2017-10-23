import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Executor {

    public static void main(String args[]) {


        Setting setting = new Setting();
        String TYPE_STATIC = "static";
        String TYPE_DYNAMIC = "dynamic";

        //String filename = "rooms1.csv";
        //String filename = "rooms2.csv";
        //String filename = "rooms3.csv";
        String filename = "room_adjust.csv";

        //ファイルの書き込み
        File rooms_file = new File(filename);
        if(!rooms_file.exists()){
            //ファイルがない場合作成
            //部屋番号、エリア番号、座標、登録する商品番号

            //Util.create_room_file(filename);
            Util.create_room_file_withoutgoods(filename);

        }

        Util.delete_file("replenishment_dy.csv");
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


        Room[] rooms_static = new Room[setting.number_of_rooms];
        Room[] rooms_dynamic = new Room[setting.number_of_rooms];

        //Roomを作成
        /*for (int i = 0; i < rooms_static.length; i++) {

            //room_element[roomId][0~4](部屋番号、エリア番号、x座標、y座標、登録する商品番号)
            rooms_static[i] = new Room(room_element[i][0], room_element[i][1], room_element[i][2], room_element[i][3], room_element[i][4], TYPE_STATIC);
            rooms_dynamic[i] = new Room(room_element[i][0], room_element[i][1], room_element[i][2], room_element[i][3], room_element[i][4], TYPE_DYNAMIC);
        }

        //0が決め打ち, 1が動的
        Simulator simulator_static = new Simulator(setting, rooms_static, 0, TYPE_STATIC);
        Simulator simulator_dynamic = new Simulator(setting, rooms_dynamic, 1, TYPE_DYNAMIC);


        //testで商品数を増やす
        for (int i = 0; i < rooms_dynamic.length; i++) {
            for (int j = 0; j < 9; j++) {
                Random rand = new Random();
                int random = rand.nextInt(3);
                rooms_static[i].register_goods(0);
                rooms_dynamic[i].register_goods(0);
            }
        }*/




        //部屋ごとの調整用
        for (int i = 0; i < rooms_static.length; i++) {

            //room_element[roomId][0~4](部屋番号、エリア番号、x座標、y座標、部屋のタイプ)
            rooms_static[i] = new Room(room_element[i][0], room_element[i][1], room_element[i][2], room_element[i][3], room_element[i][4], TYPE_STATIC, true);
            rooms_dynamic[i] = new Room(room_element[i][0], room_element[i][1], room_element[i][2], room_element[i][3], room_element[i][4], TYPE_DYNAMIC, true);
        }


        Simulator simulator_static = new Simulator(setting, rooms_static, 0, TYPE_STATIC);
        Simulator simulator_dynamic = new Simulator(setting, rooms_dynamic, 1, TYPE_DYNAMIC);

        for (int i = 0; i < rooms_dynamic.length; i++) {
            for (int j = 0; j < 10; j++) {
                Random rand = new Random();
                int random = rand.nextInt(10);
                int version;
                if(random < 7){
                    version = 0;
                }else if(random < 9){
                    version = 1;
                }else{
                    version = 2;
                }
                rooms_static[i].register_goods(version);
                rooms_dynamic[i].register_goods(version);
            }
        }
        //


        //メインのシミュレーター
        for(int i = 0; i < setting.getDays(); i++){


            int curreant_area = i%5;

            simulator_static.create_route(i, curreant_area);
            simulator_static.do_consume_simulator(i, curreant_area);
            simulator_static.do_replenishment_simulator(i);

            simulator_dynamic.create_route(i, curreant_area);
            simulator_dynamic.do_consume_simulator(i, curreant_area);
            simulator_dynamic.do_replenishment_simulator(i);

        }

        System.out.println("time_static:" + simulator_static.getTotal_time());
        System.out.println("time_dynamic:" + simulator_dynamic.getTotal_time());
        System.out.println();
        System.out.println("sales_static:" + simulator_static.get_sales());
        System.out.println("sales_dynamic:" + simulator_dynamic.get_sales());
        System.out.println("shortage_static:" + simulator_static.get_shortage());
        System.out.println("shortage_dynamic:" + simulator_dynamic.get_shortage());
        System.out.println();
        System.out.println(rooms_dynamic[0].getExpect_history().size());




        //静的の方を出力
        try{
            File file = new File("history_static" + ".csv");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

            //pw.write("roomid,day,sales,shortage,consume\n");

            for (int i = 0; i < rooms_static.length; i++) {
                ArrayList<Integer> sales_array = rooms_static[i].getGoods_list().get(0).getSales_record();
                ArrayList<Integer> shortage_array = rooms_static[i].getGoods_list().get(0).getShortage_record();
                ArrayList<Integer> consume_array = rooms_static[i].getGoods_list().get(0).getConsume_history();
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

            for (int i = 0; i < rooms_dynamic.length; i++) {
                ArrayList<Integer> sales_array = rooms_dynamic[i].getGoods_list().get(0).getSales_record();
                ArrayList<Integer> shortage_array = rooms_dynamic[i].getGoods_list().get(0).getShortage_record();
                ArrayList<Integer> consume_array = rooms_dynamic[i].getGoods_list().get(0).getConsume_history();
                ArrayList<Integer> stock_before_array = rooms_dynamic[i].getGoods_list().get(0).getStock_history_before();
                ArrayList<Integer> expect_array = rooms_dynamic[i].getExpect_history();
                ArrayList<Integer> stock_after_array = rooms_dynamic[i].getGoods_list().get(0).getStock_history_after();
                for (int j = 0; j < sales_array.size(); j++) {



                    pw.write("roomID:" + String.valueOf(i) + ", day:" + String.valueOf(j) + ", sales:" +
                                String.valueOf(sales_array.get(j)) + ", shortage:" + String.valueOf(shortage_array.get(j)) +
                                ", consume:" + String.valueOf(consume_array.get(j)) + ", stock_before:" + String.valueOf(stock_before_array.get(j)) +
                                ", expect_until_next:" + String.valueOf(expect_array.get(j)) + "\n");

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




        //valueが0でない部屋の数を出力
        /*try{
            ArrayList<Integer> tmp = simulator_static.value_rooms;
            File file = new File("value_room_num_static.csv");
            FileWriter el = new FileWriter(file);
            el.write("");
            el.close();
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.write("day,count\n");
            for (int i = 0; i < tmp.size(); i++) {
                pw.write(i + "," + tmp.get(i) + "\n");
            }
            pw.close();


            tmp = simulator_dynamic.value_rooms;
            file = new File("value_room_num_dynamic.csv");
            FileWriter ela = new FileWriter(file);
            ela.write("");
            ela.close();
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.write("day,count\n");
            for (int i = 0; i < tmp.size(); i++) {
                pw.write(i + "," + tmp.get(i) + "\n");
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }
}
