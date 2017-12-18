import org.json.JSONException;
import org.json.JSONObject;

import javafx.util.Pair;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;


public class Main {
	
	final static public int ONEMINUTE = 1;
	final static public int TENMINUTES = 10;
	final static public int ONEHOUR = 60;
	
	static public Exmo exmo;
    static public BTC_USD BTC;
    static public BCH_USD BCH;
    static public ETH_USD ETH;
    static public LTC_USD LTC;
    

    public static void waitFor(int minutes) throws InterruptedException {
    	long systemTime = System.currentTimeMillis();
    	long tenMinLapse = (long) Math.ceil(systemTime / (60000 * minutes));
    	long waitTime = (((tenMinLapse + 1) * 60000) - systemTime);
    	//long minutes = TimeUnit.MILLISECONDS.toMinutes(waitTime);
    	//long seconds = TimeUnit.MILLISECONDS.toSeconds(waitTime);
    	//System.out.print("Esperando " + String.valueOf(minutes) + "' " + String.valueOf(seconds) + "\"\n");
    	Thread.sleep(waitTime);
    }
    
    public static void setCurrentPrices(String pairs) {
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put("pair", pairs);
    	map.put("limit", "1");
        JSONObject orderBook = new JSONObject(exmo.Request("order_book", map));
        
        Double bid_top;
        Double ask_top;
        String[] pairArray = pairs.split(",");
        for (String pair : pairArray) {
        	switch (pair) {
        		case "BTC_USD":
        			bid_top = orderBook.getJSONObject("BTC_USD").getDouble("bid_top");
        	        ask_top = orderBook.getJSONObject("BTC_USD").getDouble("ask_top");
        	        BTC.open = (bid_top + ask_top) / 2;
        			break;
        		case "BCH_USD":
        			bid_top = orderBook.getJSONObject("BCH_USD").getDouble("bid_top");
        	        ask_top = orderBook.getJSONObject("BCH_USD").getDouble("ask_top");
        	        BCH.open = (bid_top + ask_top) / 2;
        			break;
        		case "ETH_USD":
        			bid_top = orderBook.getJSONObject("ETH_USD").getDouble("bid_top");
        	        ask_top = orderBook.getJSONObject("ETH_USD").getDouble("ask_top");
        	        ETH.open = (bid_top + ask_top) / 2;
        			break;
        		case "LTC_USD":
        			bid_top = orderBook.getJSONObject("LTC_USD").getDouble("bid_top");
        	        ask_top = orderBook.getJSONObject("LTC_USD").getDouble("ask_top");
        	        LTC.open = (bid_top + ask_top) / 2;
        			break;
        	}
        }
    }
    
    public static class BTC_USD {
    	public double open;
    	public double close;
    	public double low;
    	public double high;

    	public BTC_USD() {    		
    	}
    }
    
    public static class BCH_USD {
    	public double open;
    	public double close;
    	public double low;
    	public double high;

    	public BCH_USD() {
    	}
    }
    
    public static class ETH_USD {
    	public double open;
    	public double close;
    	public double low;
    	public double high;

    	public ETH_USD() {
    	}
    }
    
    public static class LTC_USD {
    	public double open;
    	public double close;
    	public double low;
    	public double high;

    	public LTC_USD() {
    	}
    }
    
    public static void main(String[] args) throws JSONException, ParseException, IOException, InterruptedException {
        exmo = new Exmo();
        BTC = new BTC_USD();
        BCH = new BCH_USD();
        ETH = new ETH_USD();
        LTC = new LTC_USD();
        Pair<Double, Double> low_high;
        
        //waitFor(ONEHOUR);
        int i = 0;
        while(true) {
            if (i == 0) {
            	setCurrentPrices("BTC_USD,BCH_USD,ETH_USD,LTC_USD");
            	System.out.println("Hora " + String.valueOf(LocalDateTime.now().getHour()) + ":");
            	System.out.println("BTC: Open=" + String.valueOf(BTC.open));
                System.out.println("BCH: Open=" + String.valueOf(BCH.open));
                System.out.println("ETH: Open=" + String.valueOf(ETH.open));
                System.out.println("LTC: Open=" + String.valueOf(LTC.open) + "\n");
            }
            if (i == 1) {
               	 	low_high = exmo.getLowHighValues("BTC_USD");
                    BTC.low = low_high.getKey();
                    BTC.high = low_high.getValue();
                    low_high = exmo.getLowHighValues("BCH_USD");
                    BCH.low = low_high.getKey();
                    BCH.high = low_high.getValue();
                    low_high = exmo.getLowHighValues("ETH_USD");
                    ETH.low = low_high.getKey();
                    ETH.high = low_high.getValue();
                    low_high = exmo.getLowHighValues("LTC_USD");
                    LTC.low = low_high.getKey();
                    LTC.high = low_high.getValue();
            }
            if (i > 1) {
		            low_high = exmo.getLowHighValues("BTC_USD");
		            if (low_high.getKey() < BTC.low) BTC.low = low_high.getKey();
		            if (low_high.getValue() > BTC.high) BTC.high = low_high.getValue();
		            low_high = exmo.getLowHighValues("BCH_USD");
		            if (low_high.getKey() < BCH.low) BCH.low = low_high.getKey();
		            if (low_high.getValue() > BCH.high) BCH.high = low_high.getValue();
		            low_high = exmo.getLowHighValues("ETH_USD");
		            if (low_high.getKey() < ETH.low) ETH.low = low_high.getKey();
		            if (low_high.getValue() > ETH.high) ETH.high = low_high.getValue();
		            low_high = exmo.getLowHighValues("LTC_USD");
		            if (low_high.getKey() < LTC.low) LTC.low = low_high.getKey();
		            if (low_high.getValue() > LTC.high) LTC.high = low_high.getValue();
            }

            
            /*
            System.out.println("BTC: Open=" + String.valueOf(BTC.open) + " Low=" + String.valueOf(BTC.low) + " High=" + String.valueOf(BTC.high));
            System.out.println("BCH: Open=" + String.valueOf(BCH.open) + " Low=" + String.valueOf(BCH.low) + " High=" + String.valueOf(BCH.high));
            System.out.println("ETH: Open=" + String.valueOf(ETH.open) + " Low=" + String.valueOf(ETH.low) + " High=" + String.valueOf(ETH.high));
            System.out.println("LTC: Open=" + String.valueOf(LTC.open) + " Low=" + String.valueOf(LTC.low) + " High=" + String.valueOf(LTC.high) + "\n");
            */
            waitFor(ONEMINUTE);
            i++;
        }

    }
}
