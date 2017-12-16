import org.json.JSONException;
import java.io.IOException;
import java.text.ParseException;


public class Main {

	//private static JSONObject userInfo, orderBook, trades;
    public static void waitForMinuteLapse() throws InterruptedException {
    	long systemTime = System.currentTimeMillis();
    	long tenMinLapse = (long) Math.ceil(systemTime / 60000);
    	long waitTime = (((tenMinLapse + 1) * 60000) - systemTime);
    	//long minutes = TimeUnit.MILLISECONDS.toMinutes(waitTime);
    	//long seconds = TimeUnit.MILLISECONDS.toSeconds(waitTime);
    	//System.out.print("Esperando " + String.valueOf(minutes) + "' " + String.valueOf(seconds) + "\"\n");
    	Thread.sleep(waitTime);
    }

    public static void main(String[] args) throws JSONException, ParseException, IOException, InterruptedException {
        Exmo exmo = new Exmo();
        exmo.getUserAccountStatus();
        
        while(true) {
            waitForMinuteLapse();
            
            exmo.getCurrentOrderBook("BTC_USD", "1");
            exmo.getLast100Trades("BTC_USD");
        }

    }
}
