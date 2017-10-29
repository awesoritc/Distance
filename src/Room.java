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
    private int room_max;

    private ArrayList<Goods> goods_list = new ArrayList<>();
    private ArrayList<Integer> expect_history = new ArrayList<>();


    private int[] distance_to_gravity;


    Room(int roomId, int areaNumber, int x_pos, int y_pos, int item_number, String simulatorType){

        this.setting = new Setting();
        this.roomId = roomId;
        this.area_number = areaNumber;
        this.x_pos = x_pos;
        this.y_pos = y_pos;

        this.distance_to_gravity = new int[setting.number_of_areas];
        this.last_replenishment = 0;


        this.simulatorType = simulatorType;
        this.room_max = setting.max_room[roomType];

        register_goods(item_number);
    }



    int roomType;// = 100;

    Room(int roomId, int areaNumber, int x_pos, int y_pos, int roomType, String simulatorType, boolean adjust){

        this.setting = new Setting();
        this.roomId = roomId;
        this.area_number = areaNumber;
        this.x_pos = x_pos;
        this.y_pos = y_pos;

        this.distance_to_gravity = new int[setting.number_of_areas];
        this.last_replenishment = 0;


        this.simulatorType = simulatorType;
        this.roomType = roomType;

        room_max = setting.max_room[roomType];
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

    public ArrayList<Integer> getExpect_history() {
        return expect_history;
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


    //adjust用
    public void register_goods_adjust(int goods_number, boolean adjust){
        Goods goods = new Goods(goods_number, roomId, setting, simulatorType, adjust);
        goods_list.add(goods);
    }


    //{shortage, sales}
    public int[] do_consume_room(int current_area){

        if(roomType == 100){
            int[] record = new int[2];
            int interval = Util.get_interval(current_area, area_number);

            for (Goods aGoods_list : goods_list) {

                //消費
                int tmp[] = aGoods_list.consume_goods();
                record[0] += tmp[0];//shortage
                record[1] += tmp[1];//sales
            }

            return record;
        }else{


            //adjust用
            int[] record = new int[2];
            int interval = Util.get_interval(current_area, area_number);

            for (Goods aGoods_list : goods_list) {

                //消費
                int tmp[] = aGoods_list.consume_goods(roomType);
                record[0] += tmp[0];//shortage
                record[1] += tmp[1];//sales
            }

            return record;


        }
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



        /*//商品の設置数を調整
        if(simulatorType.equals("dynamic")){
            adjust_amount_room();
            for (Goods aGoods_list : goods_list) {
                aGoods_list.replenishment_goods();
            }
        }*/



        return hstock;
    }


    public void write_expect_history_room(int total_shortage){
        expect_history.add(total_shortage);
    }

    public int get_room_shortage_til_next(int current_area){

        int interval = Util.get_interval(current_area, area_number);

        int total_shortage = 0;
        for (Goods aGoods_list : goods_list) {
            total_shortage += aGoods_list.getExpect_goods(interval);//aGoods_list.get_shortage_til_next(interval);
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



    public void adjust_amount_room(){

        //TODO:商品の設置数を調整する

        //roomのMaxを超えてはいけない
        //売上が0の時のための最小設置数を指定する
        //これまでの売上の履歴から比例配分
        double[] sales_sum_each_goods = new double[goods_list.size()];
        for (int i = 0; i < sales_sum_each_goods.length; i++) {
            sales_sum_each_goods[i] = 0;
        }

        int sales_length = goods_list.get(0).getSales_record().size();

        int[] share = new int[goods_list.size()];
        if(sales_length >= 25){
            //25以上の売上記録が存在した時

            for(int i = 0; i < goods_list.size(); i++){

                ArrayList<Integer> a = goods_list.get(i).getSales_record();
                for (int j = 0; j < 25; j++) {
                    //System.out.println(a.get(i));
                    sales_sum_each_goods[i] += a.get(sales_length-(j+1));
                }
            }

            int total = 0;
            for (double sales_sum_each_good : sales_sum_each_goods) {
                total += sales_sum_each_good;
            }

            int chk_amount = 0;
            for (int i = 0; i < share.length; i++) {
                share[i] = (int)Math.round(room_max * (sales_sum_each_goods[i]/total));
                if(share[i] <= 0){
                    share[i] = setting.min_amount;
                }

                System.out.println(share[i]);

                chk_amount += share[i];
            }

            if(chk_amount > room_max){
                int i = 0;
                System.out.println("max:" + room_max + ", chk:" + chk_amount);
                while(chk_amount > room_max){
                    if(share[i%goods_list.size()] <= setting.min_amount){
                        i++;
                        continue;
                    }
                    share[i%goods_list.size()] -= 1;
                    chk_amount -= 1;
                    System.out.println("max:" + room_max + ", chk:" + chk_amount);
                    i++;
                }
            }


        }else if(sales_length >= 5){
            //5以上の売上記録が存在した時

            //TODO:比例分配を設定
        }else{
            //売上記録が5以下の時

            //何もしない
        }

        for (int i = 0; i < goods_list.size(); i++) {
            goods_list.get(i).setMax_item(share[i]);
        }
    }


    public double calc_suf_rate(){

        double tmp_stock = 0;
        double tmp_max = 0;
        for(Goods aGoods: goods_list){
            tmp_stock += aGoods.getStock();
            tmp_max += aGoods.getMax_item();
        }

        return tmp_stock / tmp_max;
    }
}
