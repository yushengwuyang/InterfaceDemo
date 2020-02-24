package InfaDemo01;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import java.util.function.Function;
class DataObject{
    /**、
     * 数据的载体
     */
    private String s;
    private JSONObject json;
    public void setJson(JSONObject json) {
        this.json = json;
    }
    public void setString(String s) {
        this.s = s;
    }
    public String getString() {
        return s;
    }
    public JSONObject getJson() {
        return json;
    }
}
public  class RequestObject<T>  {
    private DataObject dataobject = new DataObject();
    Jedis jedis = new Jedis();
    RequestObject(){}
    RequestObject(JSONObject jsonObject){

        dataobject.setJson(jsonObject);
    }
    RequestObject(String s){
        dataobject.setString(s);
    }
    public String get(){
        /**
         * 拿到RequestObject里面的数据
         */
        return dataobject.getString();
    }
    RequestObject<T> map( Function<RequestObject<T>,RequestObject<T>> Mapp) {
        return Mapp.apply(this);
    }
    public RequestObject<String> getFromRedis(RequestObject<String> key_field) {
        String s = jedis.get(key_field.dataobject.getString());
            return new RequestObject<>(s);
    }
    RequestObject<String> someComputation( RequestObject<String> data){
        String s = data.dataobject.getString()+"  ";
       return new RequestObject<>(s);
    }
    RequestObject<String> getString (String data){
        String s = dataobject.getJson().getString(data);
        return new RequestObject<>(s);
    }
    void put(String s,RequestObject<String> re ){
        dataobject.getJson().put(s, re.dataobject.getString());
    }
    void put(String s,RequestObject<String> re,RequestObject<String> re2 ){
        dataobject.getJson().put(s, re.dataobject.getString() + re2.dataobject.getString());
    }
}
class text {
    public RequestObject<JSONObject> processFuncRequest(RequestObject<JSONObject> request){
        RequestObject<String> ip = request.getString("ip");
        RequestObject<String> phone = request.getString("phone");

        RequestObject<JSONObject> response = new RequestObject<>();
        RequestObject<String> ipCity =  ip.map( ip::getFromRedis);
        RequestObject<String> phoneCity =phone.map( phone::getFromRedis);


        RequestObject<String> ipPopulation = ipCity.map( ipCity::getFromRedis);
        response.put("ipPopulation", ipPopulation);
        RequestObject<String> phonePopulation = phoneCity.map( phoneCity::getFromRedis);
        response.put("phonePopulation", phonePopulation);


        RequestObject<String> data1 = request.getString("data1");
        RequestObject<String> newData1 =data1.map(data1::someComputation);
        response.put("data1", newData1);

        RequestObject<String> data2 = request.getString("data2");
        RequestObject<String> newData2 =data2.map(data2::someComputation);
        response.put("data2", newData2);
        response.put("data1+2", (newData1.map(newData1::someComputation)) , (newData2.map(newData2::someComputation)));
        return response;
    }

    public static void main(String[] args) {
        text text = new text();
        JSONObject response = new JSONObject();
        response.put("2","hello");
        RequestObject<JSONObject> requestObject = new RequestObject<>(response);

        RequestObject<String> s = requestObject.getString("2");
        System.out.println(s.map(s::getFromRedis).get());
        //RequestObject<String> requestObject1 = text.processFuncRequest(requestObject);
    }
}
