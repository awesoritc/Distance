import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Simulator {

    Setting setting;

    private int shortage, sales;

    private int num_rooms = 100;

    private Room[] rooms = new Room[num_rooms];

    private int total_time = 0;

    private ArrayList<Integer> sales_history = new ArrayList<>();
    private ArrayList<Room> replenishment_array = new ArrayList<>();

    private int route_choice;


    Simulator(Setting setting, int route_choice){

        this.setting = setting;
        this.route_choice = route_choice;

        init();
    }


    Simulator(Setting setting, Room[] rooms, int route_choice){

        this.setting = setting;
        this.route_choice = route_choice;

        init(rooms);
    }


    //初期化
    public void init(){

        shortage = 0;

        //Roomを作成する
        int a = 0;
        for(int i = 0; i < setting.number_of_areas; i++){

            for(int j = 0; j < ((int)(num_rooms/setting.number_of_areas)); j++){
                rooms[a] = new Room(i, j, setting, a);
                a++;
            }
        }

        //gravity_pointsを作成
        int[][] gravity_points = calculate_gravity_points(rooms);
        for(int i = 0; i < gravity_points.length; i++){
            System.out.println("(" + gravity_points[i][0] + "," + gravity_points[i][1] + ")");
        }


        //Goodsを登録(gravityも)
        for(int i = 0; i < setting.number_of_rooms; i++){
            rooms[i].setDistance_to_gravity(gravity_points);
            //rooms[i].register_goods(0);
            rooms[i].register_goods(new Random().nextInt(3));
        }
    }

    //Executor2本走らせるよう
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
            //rooms[i].register_goods(0);
            rooms[i].register_goods(new Random().nextInt(3));
        }
    }


    //補充する順番に部屋番号を返却
    public void create_route(int current_area, int day){
        RouteSelector routeSelector = new RouteSelector(rooms, setting);
        //routeSelector.route_time(area_number);


        //TODO:ここで補充するリストを作る

        ArrayList<Room> array = route(rooms, current_area);//routeSelector.choose_rooms(current_area);//routeSelector.choose_rooms_to_go(current_area);

        replenishment_array = array;//sort_in_order_roomId(array);
        //System.out.println("Day : " + day + ", number_rooms : " + replenishment_array.size());
        /*for (int i = 0; i < replenishment_array.size(); i++) {
            //System.out.println("id : " + replenishment_array.get(i).getId() +
            //        ", shortage : " + replenishment_array.get(i).get_room_shortage_til_next(Util.get_interval(current_area, replenishment_array.get(i).getArea_number(), setting)));
            //record("id : " + replenishment_array.get(i).getId() + ", area : " + replenishment_array.get(i).getArea_number() + ", current : "
            //        + current_area + ", shortage : " + replenishment_array.get(i).get_room_shortage_til_next(current_area) + "\n", "replenishment");
        }
        record(String.valueOf(day) + "\n\n", "replenishment");*/


        System.out.println("timeTAG:" + calculate_route_time(replenishment_array));
        total_time += calculate_route_time(replenishment_array);


    }


    //補充

    //少ないものを補充
    public void do_replenishment(int day){
        //順番が上位のものだけ補充

        System.out.println();
        for(int i = 0; i < replenishment_array.size(); i++){
            ArrayList<Integer> hstock = rooms[replenishment_array.get(i).getId()].replenishment_all();
        }

    }


    //消費
    public void do_consume(int day){

        int count = 0;
        for(int i = 0; i < num_rooms; i++){
            int[] tmp = rooms[i].do_consume();
            shortage += tmp[0];
            sales += tmp[1];
            sales_history.add(tmp[1]);
            /*record("Room : " + String.valueOf(i) + ", area : " + String.valueOf(rooms[i].getArea_number())
                    + ", sales : " + String.valueOf(tmp[1]) + ", shortage : " + String.valueOf(tmp[0])
                    + ", day : " + String.valueOf(day) + ", rep_area : " + String.valueOf(day%5) + "\n", "consume");*/

        }
    }


    public void record(String text, String filename){
        try{
            File file = new File(filename + ".csv");
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.write(text);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            System.out.println("total : (" + total[i][0] + "," + total[i][1] + ")");
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



    public ArrayList<Room> sort_in_order_roomId(ArrayList<Room> array){
        ArrayList<Room> ret_array = array;

        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                if(i < j){
                    if(ret_array.get(i).getId() > ret_array.get(i).getId()){
                        Room tmp = ret_array.get(i);
                        ret_array.set(i, ret_array.get(j));
                        ret_array.set(j, tmp);
                    }
                }

            }
        }
        return ret_array;
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
    public ArrayList<Room> route(Room[] rooms, int current_area){


        ArrayList<Room> array = new ArrayList<>();
        ArrayList<Room> route = new ArrayList<>();

        if(route_choice == 0){
            //決め打ち


            //ここからエリア固定
        for (int i = 0; i < rooms.length; i++) {
            if(rooms[i].getArea_number() == current_area){
                array.add(rooms[i]);
            }

            route = array;
        }
            //ここまで
        }else if(route_choice == 1){
            //動的に作成



            //とりあえずid順にarrayに挿入する
            for (int i = 0; i < rooms.length; i++) {
                if(rooms[i].get_value(current_area) > 0){

                    array.add(rooms[i]);
                }

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

            for (int i = 0; i < array.size(); i++) {
                System.out.println("id" + array.get(i).getId() + ", value:" + array.get(i).get_value(current_area));
            }

            //並べたものから選択する
            //TODO:ここのアルゴリズム

            //とりあえず上から20個を選択
            if(array.size() > 20){
                for (int i = 0; i < 20; i++) {
                    route.add(array.get(i));
                    counter++;
                }
            }else{
                for (int i = 0; i < array.size(); i++) {
                    route.add(array.get(i));
                    counter++;
                }
            }

        }






        //選択し終わったらid順に戻す
        for (int i = 0; i < route.size(); i++) {
            for (int j = 0; j < route.size(); j++) {

                if(i < j){
                    //iよりもjの価値の方が高ければ、jの部屋を前に変更
                    if(route.get(i).getId() > array.get(j).getId()){
                        Room tmp = route.get(i);
                        route.set(i, route.get(j));
                        route.set(j, tmp);
                    }
                }
            }
        }

        for (int i = 0; i < route.size(); i++) {
            System.out.println("確認"+route.get(i).getId());
        }

        //確認

        for (int i = 0; i < route.size(); i++) {
            //System.out.println("id:" + route.get(i).getId() + ", value:" + route.get(i).get_value(current_area));
        }


        //選択したものを返す
        return route;

    }

    public int getTotal_time() {
        return total_time;
    }
}



