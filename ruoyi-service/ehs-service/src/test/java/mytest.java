import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @author wuYang
 * @date 2022/9/19 19:02
 */

public class mytest {
    public static void main(String[] args) {
        String map = getMap("30.346865", "120.285261");
        System.out.println(map);

    }
    public static String getMap(String lat, String lng) {
        String ans = "";
        String latString = lat.toString();
        String lngString = lng.toString();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("key","bc1d930157bc84407375987cc0eb9089");
        parameters.put("location",lngString+","+latString);
        String result = HttpUtil.get("https://restapi.amap.com/v3/geocode/regeo", parameters);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject regeocode = jsonObject.getJSONObject("regeocode");
        JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
        String city = addressComponent.get("city").toString();
        String province = addressComponent.get("province").toString();
        String district = addressComponent.get("district").toString();
        String township = addressComponent.get("township").toString();
        JSONObject streetNumber = addressComponent.getJSONObject("streetNumber");
        String street = streetNumber.get("street").toString();
        String number = streetNumber.get("number").toString();
        ans+=province+city+district+township+street+number;
        return ans;

    }
}
