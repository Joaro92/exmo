import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;


public class BaseClass {
	final static public int ONEMINUTE = 1;
	final static public int TENMINUTES = 10;
	final static public int ONEHOUR = 60;

	static public Exmo exmo;
    static public BTC_USD BTC;
    static public BCH_USD BCH;
    static public ETH_USD ETH;
    static public LTC_USD LTC;
    static public Long lastMinute;
  
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
    
	//////////////////////////////////////////////////////////////////////

    public static void waitFor(int minutes) throws InterruptedException {
    	long systemTime = System.currentTimeMillis();
    	long lapse = (long) Math.ceil(systemTime / (60000 * minutes));
    	long waitTime = ((lapse + 1) * 60000 * minutes) - systemTime;
    	long min = TimeUnit.MILLISECONDS.toMinutes(waitTime);
    	long seconds = TimeUnit.MILLISECONDS.toSeconds(waitTime);
    	System.out.print("Esperando " + String.valueOf(min) + "' " + String.valueOf(seconds) + "\"\n");
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
    
    public static void setLowHighValues (String pairs) {
    	HashMap<String, String> map = new HashMap<String, String>();
		map.put("pair", pairs);
        JSONObject trades = new JSONObject(exmo.Request("trades", map));
        
        String[] pairArray = pairs.split(",");
        lastMinute = lastMinute(); // Unix TimeStamp
        Long tradeDate;
        double low = -1;
        double high = -1;
        double price;
        
        for (String pair : pairArray) {
        	JSONArray tradesArray = trades.getJSONArray(pair);
    		for (int i = 0; i < tradesArray.length(); i++) {
            	tradeDate = tradesArray.getJSONObject(i).getLong("date");
            	price = tradesArray.getJSONObject(i).getDouble("price");

            	if (tradeDate >= lastMinute) {
            		if (low < 0) {
            			low = price;
            			high = price;
            		}
            		else {
            			if (price > high)
            				high = price;
            			if (price < low)
            				low = price;
            		}
            	}
            }

        	if (low > 0 && high > 0) {
	        	switch (pair) {
	        		case "BTC_USD":
	        			if (BTC.low == 0 || low < BTC.low) BTC.low = low;
	        			if (BTC.high == 0 || high > BTC.high) BTC.high = high;
	        			break;
	        		case "BCH_USD":
	        			if (BCH.low == 0 || low < BCH.low) BCH.low = low;
	        			if (BCH.high == 0 || high > BCH.high) BCH.high = high;
	        			break;
	        		case "ETH_USD":
	        			if (ETH.low == 0 || low < ETH.low) ETH.low = low;
	        			if (ETH.high == 0 || high > ETH.high) ETH.high = high;
	        			break;
	        		case "LTC_USD":
	        			if (LTC.low == 0 || low < LTC.low) LTC.low = low;
	        			if (LTC.high == 0 || high > LTC.high) LTC.high = high;
	        			break;
	        	}
        	}
        	
        	low = -1;
        	high = -1;
        }
    }
    
    public static Long lastMinute() { // No me está trayendo los trades del último minuto, por eso le puse 2
    	long time = (long) Math.ceil((System.currentTimeMillis() + 1000) / 60000L);
        time = (time - 2) * 60L;
        return time;
    }
    
    public static Long currentHour() {
    	long time = (long) Math.ceil((System.currentTimeMillis() + 1000) / 3600000L);
        return time;
    }
    
    public static void setClosePrices(String pairs) {
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
        	        BTC.close = (bid_top + ask_top) / 2;
        			break;
        		case "BCH_USD":
        			bid_top = orderBook.getJSONObject("BCH_USD").getDouble("bid_top");
        	        ask_top = orderBook.getJSONObject("BCH_USD").getDouble("ask_top");
        	        BCH.close = (bid_top + ask_top) / 2;
        			break;
        		case "ETH_USD":
        			bid_top = orderBook.getJSONObject("ETH_USD").getDouble("bid_top");
        	        ask_top = orderBook.getJSONObject("ETH_USD").getDouble("ask_top");
        	        ETH.close = (bid_top + ask_top) / 2;
        			break;
        		case "LTC_USD":
        			bid_top = orderBook.getJSONObject("LTC_USD").getDouble("bid_top");
        	        ask_top = orderBook.getJSONObject("LTC_USD").getDouble("ask_top");
        	        LTC.close = (bid_top + ask_top) / 2;
        			break;
        	}
        }
    }
    
    public static void writeUsingFiles(String data) {
        try {
            Files.write(Paths.get("data.txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
