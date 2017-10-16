import java.io.*;

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
}
