public class Setting {

    boolean test;

    final int days = 100;//シミュレーションの期間
    final int number_of_rooms = 100;//部屋の数

    final int x_map_size = 20;//mapの大きさ20*20
    final int y_map_size = 20;//mapの大きさ20*20
    final int number_of_areas = 5;//エリアの数

    int x_bottom_divider = 10;
    int[] x_top_divider = {6, 13};
    int y_divider = 8;

    final int[][] area_borders =
            {
                    //{x_start, x_end, y_start, y_end} (x_start<=x<x_end && y_start<=y<y_end)
                    {0, x_bottom_divider, 0, y_divider},
                    {x_bottom_divider, x_map_size, 0, y_divider},
                    {x_top_divider[1], x_map_size, y_divider, y_map_size},
                    {x_top_divider[0], x_top_divider[1], y_divider, y_map_size},
                    {0, x_top_divider[0], y_divider, y_map_size}
            };

    final int moving_average_interval = 5;//移動平均に使用する
    final int border_for_replenishment = 0;


    final int max_item = 10;
    final int[][] goods_variation = {{2, 1, 10}, {3, 2, 10}, {5, 3, 10}};//{mean, variance, max_item} goodsの種類(3種類)

    final int[][] g_variation_new = {{0, 1, 10}, {1, 1, 10}, {2, 2, 10}};//商品(7:2:1)
    final int[] max_room = {30, 100, 400};//部屋ごとの最大の商品数(5:3:2) 実際はもう少し正確に
    final double[] demand_mul = {0.3, 1.0, 4.0};//需要の倍率(5:3:2)




    Setting(){
        test = false;
    }


    Setting(boolean test){
        this.test = test;

    }

    public int getDays() {
        return days;
    }

    public int getNumber_of_rooms() {
        return number_of_rooms;
    }

    public int getMax_item() {
        return max_item;
    }

    public int getX_map_size() {
        return x_map_size;
    }

    public int getY_map_size() {
        return y_map_size;
    }

    public int getNumber_of_areas() {
        return number_of_areas;
    }

    public int getX_bottom_divider() {
        return x_bottom_divider;
    }

    public void setX_bottom_divider(int x_bottom_divider) {
        this.x_bottom_divider = x_bottom_divider;
    }

    public int[] getX_top_divider() {
        return x_top_divider;
    }

    public void setX_top_divider(int[] x_top_divider) {
        this.x_top_divider = x_top_divider;
    }

    public int getY_divider() {
        return y_divider;
    }

    public void setY_divider(int y_divider) {
        this.y_divider = y_divider;
    }

    public int[][] getArea_borders() {
        return area_borders;
    }

    public int[][] getGoods_variation() {
        return goods_variation;
    }

    public int getMoving_average_interval() {
        return moving_average_interval;
    }

    public int getBorder_for_replenishment() {
        return border_for_replenishment;
    }
}
