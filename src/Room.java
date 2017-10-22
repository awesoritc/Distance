import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Room {

    Setting setting;
    private String simulatorType;

    private int roomId;
    private int x_pos;
    private int y_pos;
    private int area_number;
    private int route_number;
    private int last_replenishment;

    private ArrayList<Goods> goods_list = new ArrayList<>();


    private int[] distance_to_gravity;


    /*Room(int area_number, int route_number, Setting setting, int id){

        this.id = id;
        this.setting = setting;

        this.area_number = area_number;
        this.route_number = route_number;

        this.distance_to_gravity = new int[setting.number_of_areas];

        create_position(area_number);
    }*/


    Room(int roomId, int areaNumber, int x_pos, int y_pos, int item_number, String simulatorType){

        this.setting = new Setting();
        this.roomId = roomId;
        this.area_number = areaNumber;
        this.x_pos = x_pos;
        this.y_pos = y_pos;

        this.distance_to_gravity = new int[setting.number_of_areas];
        this.last_replenishment = 0;

        register_goods(item_number);
    }





    public int getArea_number() {
        return area_number;
    }

    public void setArea_number(int area_number) {
        this.area_number = area_number;
    }

    public int getRoute_number() {
        return route_number;
    }

    public void setRoute_number(int route_number) {
        this.route_number = route_number;
    }

    public int getX_pos() {
        return x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }

    public int[] getDistance_to_gravity() {
        return distance_to_gravity;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getLast_replenishment() {
        return last_replenishment;
    }

    public ArrayList<Goods> getGoods_list() {
        return goods_list;
    }












    public void setDistance_to_gravity(int[][] gravity_points) {

        for(int i = 0; i < setting.number_of_areas; i++){
            int distance = 0;
            if(gravity_points[i][0] > x_pos){
                distance += gravity_points[i][0] - x_pos;
            }else{
                distance += x_pos - gravity_points[i][0];
            }

            if(gravity_points[i][1] > y_pos){
                distance += gravity_points[i][1] - y_pos;
            }else{
                distance += y_pos - gravity_points[i][1];
            }

            if(distance == 0){
                distance = 1;
            }
            this.distance_to_gravity[i] = distance;
        }

        //Util.file_write("ID:" + roomId + ",(" + x_pos + "," + y_pos + ")" + "," + distance_to_gravity[0] + "," + distance_to_gravity[1] +
        //        "," + distance_to_gravity[2] + "," + distance_to_gravity[3] + "," + distance_to_gravity[4] + "\n", "distance.csv");
    }




    private void create_position(int area_number){

        Random random = new Random();
        int x_start = setting.getArea_borders()[area_number][0];
        int x_end = setting.getArea_borders()[area_number][1];
        int y_start = setting.getArea_borders()[area_number][2];
        int y_end = setting.getArea_borders()[area_number][3];

        this.x_pos = random.nextInt(x_end - x_start) + x_start;
        this.y_pos = random.nextInt(y_end - y_start) + y_start;
    }


    public void register_goods(int goods_number){
        Goods goods = new Goods(goods_number, roomId, setting, simulatorType);
        goods_list.add(goods);
    }


    //{shortage, sales}
    public int[] do_consume_room(){

        int[] record = new int[2];

        for (Goods aGoods_list : goods_list) {
            int tmp[] = aGoods_list.consume_goods();
            record[0] += tmp[0];//shortage
            record[1] += tmp[1];//sales
        }

        return record;
    }


    //補充時のストックの状態を返す
    public ArrayList<Integer> replenishment_room(int day){


        Util.file_write("day:" + day + ", area_number:" + day%5 + ", roomID:" + roomId + ", stock:" + getGoods_list().get(0).getStock() + "\n", "replenishment_dy");

        ArrayList<Integer> hstock = new ArrayList<>();
        for (Goods aGoods_list : goods_list) {
            //goods_list.get(i).setStock(goods_list.get(i).getMax_item());
            hstock.add(aGoods_list.getStock());
            aGoods_list.replenishment_goods();
        }

        last_replenishment = day;

        return hstock;
    }


    public int get_room_shortage_til_next(int current_area){

        int interval = Util.get_interval(current_area, area_number);

        int total_shortage = 0;
        for (Goods aGoods_list : goods_list) {
            total_shortage += aGoods_list.getShortage(interval);//aGoods_list.get_shortage_til_next(interval);
        }

        return total_shortage;
    }



    //TODO:get_valueの修正
    public double get_value(int current_area){


        /*if(distance_to_gravity[current_area] == 0){
            System.out.println("test0");
            return (double)get_room_shortage_til_next(current_area);
        }*/

        return (double)(get_room_shortage_til_next(current_area) / (double)distance_to_gravity[current_area]);
    }
}
