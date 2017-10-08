import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Simulator {

    Setting setting;

    private int max_item, variation;
    private int counter, shortage, sales;

    private int num_rooms = 100;

    private Room[] rooms = new Room[num_rooms];

    private int move_time;

    private ArrayList<Integer> sales_history = new ArrayList<>();
    private ArrayList<Room> replenishment_array = new ArrayList<>();

    private int x_pos, y_pos;


    Simulator(Setting setting){

        this.setting = setting;

        init();
    }


    //初期化
    public void init(){

        max_item = 10;
        variation = 1;
        move_time = 5;

        counter = 0;
        shortage = 0;

        //Roomを作成する
        int a = 0;
        for(int i = 0; i < setting.number_of_areas; i++){

            for(int j = 0; j < ((int)(num_rooms/setting.number_of_areas)); j++){
                rooms[a] = new Room(i, j, setting, a);
                a++;
            }
        }
        System.out.println("a" + a);

        //gravity_pointsを作成
        int[][] gravity_points = calculate_gravity_points(rooms);


        //Goodsを登録(gravityも)
        for(int i = 0; i < setting.number_of_rooms; i++){
            rooms[i].setDistance_to_gravity(gravity_points);
            //rooms[i].register_goods(0);
            rooms[i].register_goods(new Random().nextInt(3));
        }
    }


    //補充する順番に部屋番号を返却
    public void create_route(int area_number, int day){
        RouteSelector routeSelector = new RouteSelector(rooms, setting);
        //routeSelector.route_time(area_number);


        //TODO:ここで補充するリストを作る

        replenishment_array = sort_in_order_roomId(routeSelector.choose_rooms_to_go(area_number));
        //System.out.println("Day : " + day + ", number_rooms : " + replenishment_array.size());
    }


    //補充

    //ルート決め打ち
    public void do_replenishment(int current_area, int day){

        for(int i = 0; i < 20; i++){
            ArrayList<Integer> hstock = rooms[i + current_area*20].replenishment_all();
            /*record("Room : " + String.valueOf(i + current_area*20) + ", area : " + rooms[i + current_area*20].getArea_number() +
                    ", stock : " + String.valueOf(hstock.get(0)) + ", rep_area : " + String.valueOf(current_area) +
                    ", day : " + day + "\n", "replenishment");*/
        }

        //record("\n", "replenishment");
    }

    //少ないものを補充
    public void do_replenishment(int day){
        //順番が上位のものだけ補充

        System.out.println();
        for(int i = 0; i < replenishment_array.size(); i++){
            ArrayList<Integer> hstock = rooms[replenishment_array.get(i).getId()].replenishment_all();
            /*record("Room : " + String.valueOf(replenishment_array.get(i).getId()) + ", area : " + rooms[replenishment_array.get(i).getId()].getArea_number() +
                    ", stock : " + String.valueOf(hstock.get(0)) + ", rep_area : " + String.valueOf(day%5) +
                    ", day : " + day + "\n", "replenishment");*/
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


    //現在の状況を記録する
    public void record(int sales, int shortage, int current_area, int room_num){
            String text = "Room : " + String.valueOf(room_num) + ", area :" + current_area + ", sales : " + String.valueOf(sales) + ", shortage : " + String.valueOf(shortage) + "\n";
            try{
                File file = new File("norma.csv");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                pw.write(text);
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
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

        int a = 0;
        for (int i = 0; i < setting.getNumber_of_areas(); i++) {
            for (int j = 0; j < setting.number_of_rooms/setting.number_of_areas; j++) {
                    total[i][0] += rooms[a].getX_pos();
                    total[i][1] += rooms[a].getY_pos();
                    a++;
            }
            total[i][0] = (int)Math.round(total[i][0] / rooms.length);
            total[i][1] = (int)Math.round(total[i][1] / rooms.length);
        }


        return total;
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
}



