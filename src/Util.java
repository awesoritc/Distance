import java.io.*;
import java.util.Random;

public class Util {

    //次までの期間を返す
    public static int get_interval(int current_area, int area_num){

        Setting setting = new Setting();

        int interval = 0;
        if(area_num > current_area){
            interval = area_num - current_area;
        }else{
            interval = area_num + setting.getNumber_of_areas() - current_area;
        }

        return interval;
    }



    public static void p(String t){

        System.out.println(t);
    }




    public static void file_write(String text, String filename){
        try{
            File file = new File(filename + ".csv");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.write(text);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //{x_pos, y_pos}
    public static int[] create_position(int area_number){

        Setting setting = new Setting();

        Random random = new Random();
        int x_start = setting.getArea_borders()[area_number][0];
        int x_end = setting.getArea_borders()[area_number][1];
        int y_start = setting.getArea_borders()[area_number][2];
        int y_end = setting.getArea_borders()[area_number][3];

        int x_pos = random.nextInt(x_end - x_start) + x_start;
        int y_pos = random.nextInt(y_end - y_start) + y_start;

        return new int[]{x_pos, y_pos};
    }


    public static void create_room_file(String filename){
        Setting setting = new Setting();
        File rooms_file = new File(filename);
        if(!rooms_file.exists()){
            //ファイルがない場合作成
            //部屋番号、エリア番号、座標、登録する商品番号

            int a = 0;
            try{

                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(rooms_file, true)));

                for(int i = 0; i < setting.number_of_areas; i++){

                    for(int j = 0; j < ((int)(setting.number_of_rooms/setting.number_of_areas)); j++){

                        int roomid = a;
                        int areanumber = i;
                        int[] pos = Util.create_position(i);
                        Random rand = new Random();
                        int goodsnumber = rand.nextInt(setting.goods_variation.length);


                        pw.write(String.valueOf(roomid) + "," + String.valueOf(areanumber) + "," +
                                "(" + String.valueOf(pos[0]) + ":" + String.valueOf(pos[1]) + ")," + String.valueOf(goodsnumber) + "\n");



                        //Util.file_write(String.valueOf(a) + "," + String.valueOf(i) + "," +
                        //        "(" + String.valueOf(pos[0]) + ":" + String.valueOf(pos[0]) + ")," + String.valueOf(goodsnumber), "rooms1.csv");


                        a++;
                    }
                }

                pw.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void create_room_file_withoutgoods(String filename){
        Setting setting = new Setting();
        File rooms_file = new File(filename);
        if(!rooms_file.exists()){
            //ファイルがない場合作成
            //部屋番号、エリア番号、座標、登録する商品番号

            int a = 0;
            try{

                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(rooms_file, true)));

                for(int i = 0; i < setting.number_of_areas; i++){

                    for(int j = 0; j < ((int)(setting.number_of_rooms/setting.number_of_areas)); j++){

                        int roomid = a;
                        int areanumber = i;
                        int[] pos = Util.create_position(i);
                        Random rand = new Random();
                        int random = rand.nextInt(10);
                        int type;
                        if(random < 5){
                            type = 0;
                        }else if(random < 7){
                            type = 1;
                        }else{
                            type = 2;
                        }


                        pw.write(String.valueOf(roomid) + "," + String.valueOf(areanumber) + "," +
                                "(" + String.valueOf(pos[0]) + ":" + String.valueOf(pos[1]) + ")," + String.valueOf(type) + "\n");



                        //Util.file_write(String.valueOf(a) + "," + String.valueOf(i) + "," +
                        //        "(" + String.valueOf(pos[0]) + ":" + String.valueOf(pos[0]) + ")," + String.valueOf(goodsnumber), "rooms1.csv");


                        a++;
                    }
                }

                pw.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static int[][] read_room_file(String filename){

        Setting setting = new Setting();
        //room_file読み込み
        Room[] rooms = new Room[setting.number_of_rooms];

        int[][] ret = new int[setting.number_of_rooms/*100*/][5/**/];//100部屋の5つの要素を返す


        //forここから
        try{
            File r_file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(r_file));

            for (int i = 0; i < setting.number_of_rooms; i++) {
                String tmp_room = reader.readLine();
                String[] a = tmp_room.split(",");

                int roomid = Integer.valueOf(a[0]);
                int areanumber = Integer.valueOf(a[1]);
                String[] pos = a[2].split(":");
                int x_pos = Integer.valueOf(pos[0].substring(1));
                int y_pos = Integer.valueOf(pos[1].substring(0, pos[1].length()-1));
                int itemnumber = Integer.valueOf(a[3]);

                int[] tmp = {roomid, areanumber, x_pos, y_pos, itemnumber};

                ret[i] = tmp;
            }

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Roomの要素を返却
        return ret;





        //ここまで

    }


    public static void delete_file(String filename){
        try {
            File file_static = new File(filename);
            if (file_static.exists()) {
                new FileWriter(file_static).write("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
