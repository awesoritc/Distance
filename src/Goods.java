import java.util.ArrayList;

public class Goods {

    private int average;
    private double variance;
    private int stock;
    private int max_item;

    private ArrayList<Integer> sales_record;

    private Setting setting;



    Goods(int[] variation, Setting setting){
        this.setting = setting;

        this.average = variation[0];
        this.variance = variation[1];
        this.stock = variation[2];
        this.max_item = variation[2];

        sales_record = new ArrayList<>();
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

    //{shortage, sales}
    public int[] consume(){
        NormalDistribution nd = new NormalDistribution(average, variance);
        int consume = (int) Math.round(nd.random());
        if(consume < 0){
            consume = 0;
        }
        int shortage = 0;
        int sales = 0;

        if(stock > consume){
            shortage = 0;
            sales = consume;
            stock -= consume;
            sales_record.add(sales);
        }else if(stock > 0){
            shortage = consume - stock;
            sales = stock;
            stock = 0;
            sales_record.add(sales);
        }else{
            shortage = consume;
            sales = 0;
            sales_record.add(sales);
        }
        return new int[]{shortage, sales};
    }

    public void replenishment(){
        stock = max_item;
    }



    public int get_shortage_til_next(int interval){

        int tmp = 0;
        if(sales_record.size() > setting.getMoving_average_interval()){
            for(int i = 0; i < setting.getMoving_average_interval(); i++){
                tmp += sales_record.get(sales_record.size()-(i+1));
                //System.out.println(i);
            }
        }else{
            return 0;
        }


        int consume_til_next = (tmp / setting.getMoving_average_interval()) * interval;

        if(consume_til_next > stock){
            //System.out.println("c" + String.valueOf(consume_til_next - stock));
            return consume_til_next - stock;
        }

        return 0;
    }
}
