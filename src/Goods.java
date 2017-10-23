import java.io.*;
import java.util.ArrayList;

public class Goods {

    private int goodsnumber;
    private int average;
    private double variance;
    private int stock;
    private int max_item;
    private int roomId;

    private ArrayList<Integer> sales_record, shortage_record, consume_history, stock_history_before, stock_history_after;//売り上げ、欠品数、需用量を書き出し

    private Setting setting;
    private String simulatorType;



    Goods(int goods_variation_num, int roomId, Setting setting, String simulatorType){
        this.setting = setting;
        this.simulatorType = simulatorType;
        this.goodsnumber = goods_variation_num;

        int[] variation = setting.getGoods_variation()[goods_variation_num];

        this.average = variation[0];
        this.variance = variation[1];
        this.stock = variation[2];
        this.max_item = variation[2];
        this.roomId = roomId;

        sales_record = new ArrayList<>();
        shortage_record = new ArrayList<>();
        consume_history = new ArrayList<>();
        stock_history_before = new ArrayList<>();
        stock_history_after = new ArrayList<>();
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getMax_item() {
        return max_item;
    }

    public void setMax_item(int max_item) {
        this.max_item = max_item;
    }

    public ArrayList<Integer> getSales_record() {
        return sales_record;
    }

    public ArrayList<Integer> getShortage_record() {
        return shortage_record;
    }

    public ArrayList<Integer> getConsume_history() {
        return consume_history;
    }

    public ArrayList<Integer> getStock_history_before() {
        return stock_history_before;
    }

    public ArrayList<Integer> getStock_history_after() {
        return stock_history_after;
    }

    public int getGoodsnumber() {
        return goodsnumber;
    }

    //{shortage, sales}
    public int[] consume_goods(){

        stock_history_before.add(stock);


        NormalDistribution nd = new NormalDistribution(average, variance);
        int consume = (int) Math.round(nd.random());
        if(consume < 0){
            consume = 0;
        }
        if(setting.test){
            System.out.println("consume : " + consume);
        }
        int shortage = 0;
        int sales = 0;

        if(stock > consume){
            shortage = 0;
            sales = consume;
            stock -= consume;
        }else if(stock > 0){
            shortage = consume - stock;
            sales = stock;
            stock = 0;
        }else{
            shortage = consume;
            sales = 0;
        }
        sales_record.add(sales);
        shortage_record.add(shortage);
        consume_history.add(consume);
        stock_history_after.add(stock);


        if(setting.test){
            System.out.println("sales : " + sales + ", shortage : " + shortage);
        }
        return new int[]{shortage, sales};
    }

    public void replenishment_goods(){
        stock = max_item;
    }



    public int get_shortage_til_next(int interval){

        int tmp = 0;
        if(sales_record.size() > setting.getMoving_average_interval()){
            for(int i = 0; i < setting.getMoving_average_interval(); i++){
                tmp += sales_record.get(sales_record.size()-(i+1));
            }
        }else{
            return 0;
        }


        int consume_til_next = (tmp / setting.getMoving_average_interval()) * interval;
        if(setting.test){
            String s = "";
            for(int i = 0; i < setting.getMoving_average_interval(); i++){
                s += String.valueOf(sales_record.get(sales_record.size()-(i+1))) + " ";
            }
        }


        if(consume_til_next > stock){
            return consume_til_next - stock;
        }

        return 0;
    }


    //1週間の平均の値を5週間分とってきて計算
    public double get_shortage_next_5days(int current_area){

        double tmp = 0;
        if(sales_record.size() > setting.getMoving_average_interval() * 5){
            for(int i = 0; i < setting.getMoving_average_interval() * 5; i++){
                tmp += sales_record.get(sales_record.size()-(i+1));
            }

            return (double)Math.round(tmp / 5.0);
        }/*else if(sales_record.size() > 5){

        }*/else{
            return 0;
        }

    }



    public int getExpect_goods(int interval){
        //5日間の売り上げの移動平均で消費量を予測

        int tmp = 0;
        if(sales_record.size() >= setting.getMoving_average_interval() * 5){
            //25以上売り上げデータがある時
            for(int i = 0; i < setting.getMoving_average_interval() * 5; i++){
                tmp += sales_record.get(sales_record.size()-(i+1));
            }

            int cons = Math.round((int)((double)Math.round(tmp / (setting.getMoving_average_interval() * 5)) * interval));
            //return ((double)Math.round(tmp / (setting.getMoving_average_interval() * 5)) * interval);

            if(cons > stock){
                return cons - stock;
            }else{
                return 0;
            }

        }else if(sales_record.size() >= 5){
            //5以上売り上げデータがある時
            return get_shortage_til_next(interval);
        }else{
            //売り上げデータがないとき
            return 0;
        }
    }




    //TODO:ここの処理を考慮
    //商品のMaxの調整用の関数
    public void adjust_max(){

        //過去のstockの履歴から0となっている場所が多すぎる場合、Maxを増やす,少なすぎる場合Maxを減らす
        //増やす場合に減らす場所を決める必要がある
        //Roomから呼び出し、最大の商品数を超えないように設定

        //直近のstock数の0の個数を計算
        int count = 0;
        for (int i = 0; i < 10; i++) {
            if(stock_history_before.get(stock_history_before.size()-(1+i)) == 0){
                count++;
            }
        }
        if(count > 5){
            //stock少なすぎ
        }else if(count < 1){
            //stock多すぎ
        }else{
            //このままでok
        }
    }
}
