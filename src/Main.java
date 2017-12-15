import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;

public class Main {

	//private static JSONObject userInfo, orderBook, trades;
    

    public static void main(String[] args) throws JSONException, ParseException, IOException {
        Exmo exmo = new Exmo();
        
        exmo.getUserAccountStatus();
        exmo.getCurrentOrderBook("BTC_USD", "1");
        exmo.getLast100Trades("BTC_USD");
         
    }
}
