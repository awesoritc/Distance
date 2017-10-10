public class Util {

    //次までの期間を返す
    public static int get_interval(int current_area, int area_num, Setting setting){

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
}
