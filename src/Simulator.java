import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Simulator {

    Setting setting;
    private String simulatorType;

    private int shortage, sales;

    private int num_rooms = 100;

    private int longest_non_replenishment = 5*5;

    private Room[] rooms = new Room[num_rooms];

    private int total_time = 0;

    private ArrayList<Integer> sales_history = new ArrayList<>();
    private ArrayList<Room> replenishment_array = new ArrayList<>();

    private int route_choice;


    Simulator(Setting setting, Room[] rooms, int route_choice, String simulateType){

        this.setting = setting;
        this.route_choice = route_choice;
        this.simulatorType = simulateType;

        init(rooms);
    }

    //初期化Executor2本走らせるよう
    public void init(Room[] def_room){

        shortage = 0;

        rooms = def_room;

        //gravity_pointsを作成
        int[][] gravity_points = calculate_gravity_points(rooms);
        for(int i = 0; i < gravity_points.length; i++){
            System.out.println("(" + gravity_points[i][0] + "," + gravity_points[i][1] + ")");
        }


        //Goodsを登録(gravityも)
        for(int i = 0; i < setting.number_of_rooms; i++){
            rooms[i].setDistance_to_gravity(gravity_points);
            //rooms[i].register_goods(1);
            //rooms[i].register_goods(new Random().nextInt(3));
        }



        //ファイルの項目を追加
        try{
            File file = new File(simulatorType + ".csv");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

            pw.write("roomid,day,current_area,room_area,stock,shortage_until_next,distance_to_current_area,value\n");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try{
            File file = new File("value_" + simulatorType + ".csv");
            FileWriter writer = new FileWriter(file);
            writer.write("day" + ",current" + ",roomID" + ",value\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //補充する順番に部屋番号を返却
    public void create_route(int day, int current_area){
        RouteSelector routeSelector = new RouteSelector(rooms, setting);
        //routeSelector.route_time(area_number);


        //TODO:ここで補充するリストを作る

        ArrayList<Room> array = route(rooms, current_area, day);//routeSelector.choose_rooms(current_area);//routeSelector.choose_rooms_to_go(current_area);

        replenishment_array = array;//sort_in_order_roomId(array);

        total_time += calculate_route_time(replenishment_array);


        for (int i = 0; i < rooms.length; i++) {
            rooms[i].write_expect_history_room(rooms[i].get_room_shortage_til_next(current_area));
        }

        //補充ルートを書き出し
        try {
            //ルート書き出し
            File file = new File(simulatorType + ".csv");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

            for (int i = 0; i < replenishment_array.size(); i++) {
                Room tm = replenishment_array.get(i);


                pw.write(tm.getRoomId() + "," + day + "," + current_area + "," + tm.getArea_number() +
                        "," + tm.getGoods_list().get(0).getStock() + "," + tm.get_room_shortage_til_next(current_area) +
                        "," + tm.getDistance_to_gravity()[current_area] + "," + tm.get_value(current_area) + "\n");
            }
            //pw.write("\n");

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //補充

    //少ないものを補充
    public void do_replenishment_simulator(int day){
        //順番が上位のものだけ補充

        //System.out.println();
        for(int i = 0; i < replenishment_array.size(); i++){
            ArrayList<Integer> hstock = rooms[replenishment_array.get(i).getRoomId()].replenishment_room(day);
        }

    }


    //消費
    public void do_consume_simulator(int day, int current_area){

        for(int i = 0; i < num_rooms; i++){
            int[] tmp = rooms[i].do_consume_room(current_area);
            shortage += tmp[0];
            sales += tmp[1];
            sales_history.add(tmp[1]);
        }
    }


    //重心(x,y)
    public int[][] calculate_gravity_points(Room[] rooms){
        int[][] total = new int[setting.getNumber_of_areas()][2];
        int num_rooms_per_area = setting.number_of_rooms/setting.number_of_areas;

        int a = 0;
        for (int i = 0; i < setting.getNumber_of_areas(); i++) {
            for (int j = 0; j < num_rooms_per_area; j++) {
                    total[i][0] += rooms[a].getX_pos();
                    total[i][1] += rooms[a].getY_pos();
                    a++;
            }
            //System.out.println("total : (" + total[i][0] + "," + total[i][1] + ")");
            total[i][0] = (int)Math.round(total[i][0] / num_rooms_per_area);
            total[i][1] = (int)Math.round(total[i][1] / num_rooms_per_area);
        }


        return total;
    }



    public int calculate_route_time(ArrayList<Room> route){


        if(route.size() != 0){

            int current_x = route.get(0).getX_pos();
            int current_y = route.get(0).getY_pos();

            int routetime = 0;
            for (int i = 1; i < route.size(); i++) {

                int next_x = route.get(i).getX_pos();
                int next_y = route.get(i).getY_pos();
                int next_time = 0;
                if(current_x > next_x){
                    next_time += current_x - next_x;
                }else{
                    next_time += next_x - current_x;
                }
                if(current_y > next_y){
                    next_time += current_y - next_y;
                }else{
                    next_time += next_y - current_y;
                }

                routetime += next_time;
            }

            return routetime;
        }

        return 0;
    }


    public int get_sales(){
        //System.out.println(sales);
        return sales;
    }


    public int get_shortage(){
        //System.out.println(shortage);
        return shortage;
    }





    int counter = 0;
    int counterb = 0;
    ArrayList<Integer> value_rooms = new ArrayList<>();

    public ArrayList<Room> route(Room[] rooms, int current_area, int day){


        ArrayList<Room> array = new ArrayList<>();
        ArrayList<Room> route = new ArrayList<>();


        //roomのvalueを表示
        for (int i = 0; i < rooms.length; i++) {
            try{
                File file = new File("value_" + simulatorType + ".csv");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                //pw.write("day:" + day + ", current:" + current_area + ", roomID:" + i + ", value:" + rooms[i].get_value(current_area) + "\n");
                pw.write( day + "," + current_area + "," + i + "," + rooms[i].get_value(current_area) + "\n");
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(route_choice == 0){
            //決め打ち


            //ここからエリア固定
            int val_counter = 0;
        for (int i = 0; i < rooms.length; i++) {
            if(rooms[i].getArea_number() == current_area){
                array.add(rooms[i]);
            }

            if(rooms[i].get_value(current_area) > 0){
                val_counter++;
            }
        }
        value_rooms.add(val_counter);
        //ここまで

        return array;


        }else if(route_choice == 1){
            //動的に作成



            //とりあえずvalueが0でない部屋をid順にarrayに挿入する
            int val_counter = 0;
            for (int i = 0; i < rooms.length; i++) {
                if(rooms[i].get_value(current_area) > 0){

                    array.add(rooms[i]);

                    val_counter++;
                }else{
                    //valueが0でも期間をすぎていれば追加
                    if((day - rooms[i].getLast_replenishment()) > longest_non_replenishment){
                        array.add(rooms[i]);
                    }
                }
            }
            value_rooms.add(val_counter);

            //TODO:ここでvalueが0でもしばらく補充されていない部屋は補充する

            //valueが0以上のものが存在しなかった場合、current_areaの部屋を補充
            if(array.size() == 0){

                for (int i = 0; i < setting.number_of_rooms; i++) {
                    if(rooms[i].getArea_number() == current_area){
                        array.add(rooms[i]);
                    }
                }
                return array;
            }


            //価値順に並べる
            for (int i = 0; i < array.size(); i++) {
                for (int j = 0; j < array.size(); j++) {

                    if(i < j){
                        //iよりもjの価値の方が高ければ、jの部屋を前に変更
                        if(array.get(i).get_value(current_area) < array.get(j).get_value(current_area)){
                            Room tmp = array.get(i);
                            array.set(i, array.get(j));
                            array.set(j, tmp);
                        }
                    }
                }
            }

            /*for (int i = 0; i < array.size(); i++) {
                System.out.println("id" + array.get(i).getRoomId() + ", value:" + array.get(i).get_value(current_area));
            }*/

            //並べたものから選択する
            //TODO:ここのアルゴリズム

            //まず期間をすぎているものを追加
            if(array.size() > 20){
                for (int i = 0; i < array.size(); i++) {
                    if((day - array.get(i).getLast_replenishment()) > longest_non_replenishment){
                        route.add(array.get(i));
                        array.remove(i);
                    }
                }

                if(route.size() > 20){
                    //期間をすぎているものが20こ以上ある時

                    //現在のエリアから近いもの順に並べrouteにする

                    for (int i = 0; i < route.size(); i++) {
                        for (int j = 0; j < route.size(); j++) {
                            if(i < j){
                                if(route.get(i).getDistance_to_gravity()[current_area] > route.get(j).getDistance_to_gravity()[current_area]){
                                    Room tmp = route.get(i);
                                    route.set(i, route.get(i));
                                    route.set(j, tmp);
                                }
                            }
                        }
                    }

                    while(route.size() > 20){
                        route.remove(route.size()-1);
                    }
                }else{

                    while(route.size() < 20){
                        route.add(array.get(0));
                        array.remove(0);
                    }
                }
            }


            /*
            //とりあえず上から20個を選択
            if(array.size() > 20){
                for (int i = 0; i < 20; i++) {
                    route.add(array.get(i));
                    counter++;
                }
                counterb++;
            }else{
                for (int i = 0; i < array.size(); i++) {
                    route.add(array.get(i));
                    counter++;
                }
            }

            */

            //選択し終わったらid順に戻す
            for (int i = 0; i < route.size(); i++) {
                for (int j = 0; j < route.size(); j++) {

                    if(i < j){
                        //iよりもjの価値の方が高ければ、jの部屋を前に変更
                        if(route.get(i).getRoomId() > route.get(j).getRoomId()){
                            Room tmp = route.get(i);
                            route.set(i, route.get(j));
                            route.set(j, tmp);
                        }
                    }
                }
            }

            return route;
        }

        return null;
    }



    //greedy
    public ArrayList<Room> greedy(int current_area, int day,  Room[] rs){




        /*for (int i = 0; i < rs.length; i++) {
            System.out.println("day:" + day + ", id:" + rs[i].getRoomId() + ", suf_rate:" + rs[i].calc_suf_rate() + "");
        }System.out.println("");*/


        ArrayList<Room> greedy_route = new ArrayList<>();
        if(day < 5){
            //固定

            for (int i = 0; i < 20; i++) {
                System.out.println(i + current_area*20);
                greedy_route.add(rs[i + current_area*20]);
                if(greedy_route.size() >= 20){
                    break;
                }
            }

            System.out.println("固定:" + day);
        }else{


            int routesize = 0;
            //充足率低い順に並べる
            for (int i = 0; i < rs.length; i++) {
                for (int j = 0; j < rs.length; j++) {
                    if(i < j){
                        if(rs[i].calc_suf_rate() > rs[j].calc_suf_rate()){
                            Room tmp = rs[i];
                            rs[i] = rs[j];
                            rs[j] = tmp;
                        }
                    }
                }
            }

            for (int i = 0; i < rs.length; i++) {
                if(greedy_route.size() < 20){
                    greedy_route.add(rs[i]);
                }
                /*if(routesize < 100){
                    greedy_route.add(rs[i]);
                    if(i != 0){
                        routesize = calculate_route_time(greedy_route);
                    }
                }*/
            }

            greedy_route = set_id_order(greedy_route);


            System.out.println("Day:" + day);
            for (int i = 0; i < greedy_route.size(); i++) {
                System.out.println(greedy_route.get(i).getRoomId());
            }
            System.out.println("");

            /*if(greedy_route.size() < 20){
                //なければ固定のルートを補充
                for (int i = 0; i < 20; i++) {
                    greedy_route.add(rs[i + current_area*20]);
                    if(greedy_route.size() >= 20){
                        break;
                    }
                }
            }*/
        }

        return greedy_route;
    }


    public ArrayList<Room> set_id_order(ArrayList<Room> route){
        for (int i = 0; i < route.size(); i++) {
            for (int j = 0; j < route.size(); j++) {

                if(i < j){
                    //iよりもjの価値の方が高ければ、jの部屋を前に変更
                    if(route.get(i).getRoomId() > route.get(j).getRoomId()){
                        Room tmp = route.get(i);
                        route.set(i, route.get(j));
                        route.set(j, tmp);
                    }
                }
            }
        }
        return route;
    }

    public int getTotal_time() {
        return total_time;
    }
}



