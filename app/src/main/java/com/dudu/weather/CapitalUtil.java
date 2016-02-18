package com.dudu.weather;

import java.util.HashMap;
import java.util.Map;


public class CapitalUtil {

    private static final Map<String, String> proCapMap = new HashMap<String, String>() {
        {
            put("黑龙江", "哈尔滨");
            put("吉林", "长春");
            put("辽宁", "沈阳");
            put("河北", "石家庄");
            put("甘肃", "兰州");
            put("青海", "西宁");
            put("陕西", "西安");
            put("河南", "郑州");
            put("山东", "济南");
            put("山西", "太原");
            put("安徽", "合肥");
            put("湖北", "武汉");
            put("湖南", "长沙");
            put("江苏", "南京");
            put("四川", "成都");
            put("贵州", "贵阳");
            put("云南", "昆明");
            put("浙江", "杭州");
            put("江西", "南昌");
            put("广东", "广州");
            put("福建", "福州");
            put("台湾", "台北");
            put("海南", "海口");
            put("新疆", "乌鲁木齐");
            put("内蒙古", "内蒙古");
            put("宁夏", "银川");
            put("广西", "南宁");
            put("西藏", "拉萨");
            //几个直辖市的已经做处理了
        }

    };

    /**
     * @param province 根据省名查看省会，已经省略了“省”字的
     * @return 省会名称
     */
    public static String getCapital(String province) {
        return proCapMap.get(province);
    }
}
