import java.util.ArrayList;

public class RouteSelector {

    //補充するルートを決定

    private Setting setting;
    private Room[] rooms;
    private float[] value;

    private ArrayList<Room> area_rooms;

    private ArrayList<Room> route_list;


    RouteSelector(Room[] rooms, Setting setting){
        this.setting = setting;

        this.rooms = rooms;
        this.value = new float[rooms.length];

        area_rooms = new ArrayList<>();
        route_list = new ArrayList<>();
    }


    //TODO:
    //巡回する部屋を決める
    //巡回した時の時間を返す



    public ArrayList<Room> choose_rooms_to_go(int current_area){
        System.out.println("route_selector");
        int max_area = setting.getNumber_of_areas() - 1;

        //欠品するかどうかを計算し、補充に回る部屋を決める
        int days_til_next = 0;

        ArrayList<Integer> array = new ArrayList<>();
        for(int i = 0; i < rooms.length; i++){

            if(rooms[i].get_room_shortage_til_next(Util.get_interval(current_area, rooms[i].getArea_number(), setting)) > 0){
                route_list.add(rooms[i]);
            }
        }

        if(route_list.size() > 20){
            //sort
            route_list = sort_array_by_value(route_list, current_area);

            ArrayList<Room> lis = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                lis.add(route_list.get(i));
            }
            return lis;
        }



        if(route_list.size() > 0){
            System.out.println("Time : " + calculate_route_time(route_list));
        }

        return route_list;
    }


    //補充のルートを計算
    //追加した部屋のルートさえ決めれば自動的に決定する



    //かかった距離
    public int calculate_route_time(ArrayList<Room> array){

        int time = 0;
        int x_now = array.get(0).getX_pos();
        int y_now = array.get(0).getY_pos();
        for(int i = 1; i < array.size(); i++){
            int x_next = array.get(i).getX_pos();

            if(x_now > x_next){
                time += x_now - x_next;
            }else{
                time += x_next - x_now;
            }
            int y_next = array.get(i).getY_pos();
            if(y_now > y_next){
                time += y_now - y_next;
            }else{
                time += y_next - y_now;
            }

            x_now = x_next;
            y_now = y_next;
        }
        return time;
    }



    public ArrayList<Room> sort_array_by_value(ArrayList<Room> array, int current_area){

        ArrayList<Room> ret_array = array;

        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                if(i < j){
                    //時間かかりすぎる
                    if((ret_array.get(i).get_room_shortage_til_next(Util.get_interval(current_area, ret_array.get(i).getArea_number(), setting)) / ret_array.get(i).getDistance_to_gravity()[current_area]) <
                            (ret_array.get(j).get_room_shortage_til_next(Util.get_interval(current_area, ret_array.get(i).getArea_number(), setting)) / ret_array.get(i).getDistance_to_gravity()[current_area])){
                        Room tmp = ret_array.get(i);
                        ret_array.set(i, ret_array.get(j));
                        ret_array.set(j, tmp);
                    }
                }
            }
        }

        return ret_array;
    }


    //public void route_optimizer(){}
}
