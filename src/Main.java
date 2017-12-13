import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.*;

public class Main {

	public static String unixSecondsToString(String unixSecondsString) {
		long unixSeconds = Long.parseLong(unixSecondsString);
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT-3")); 
	    String formattedDate = sdf.format(date);
	    return formattedDate;
	}
    public static void main(String[] args) throws JSONException, ParseException {

        Exmo e = new Exmo("public key","secret key");

        JSONObject user_info = new JSONObject(e.Request("user_info", null));
        String server_date = unixSecondsToString(user_info.get("server_date").toString());
        JSONObject balances = user_info.getJSONObject("balances");
        String USD = balances.getString("USD");
        System.out.println("Server Date: " + server_date);
        System.out.println("Account Balance:");
        System.out.println(" USD = " + USD);
        
        JSONObject ticker = new JSONObject(e.Request("ticker", null));
        String BTC_USD = ticker.getJSONObject("BTC_USD").getString("last_trade");
        System.out.println("--------------------------");
        System.out.println("Currency trade prices:");
        System.out.println(" BTC -> USD = " + BTC_USD);
        System.out.println(ticker);
        /*
        String result2 = e.Request("user_cancelled_orders", new HashMap<String, String>() {{
            put("limit", "2");
            put("offset", "0");
        }});
        System.out.println(result2);
		*/
    }
}
