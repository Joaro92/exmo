/**
 * Created by Admin on 2/18/2016.
 */

import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;


public class Exmo {
    private static long _nonce;
    private String _key;
    private String _secret;

    public Exmo() {
        _nonce = System.nanoTime();
        String keys = null;
		try { keys = hexToAscii(readFile("keys.txt").findFirst().get()); }
		catch (IOException e) { e.printStackTrace(); }
    	_key = keys.substring(0, 42);
    	_secret = keys.substring(42);
    }
    
    public JSONObject getUserAccountStatus() {
    	JSONObject ui = new JSONObject(this.Request("user_info", null));
    	
    	String server_date = unixSecondsToString(ui.get("server_date").toString());
        JSONObject balances = ui.getJSONObject("balances");
        String USD = balances.getString("USD");
        System.out.println("Server Date: " + server_date);
        System.out.println("Account Balance:");
        System.out.println(" USD = " + USD);
    	return ui;
    }
    
    public JSONObject getCurrentOrderBook(String pair, String limit) {
        HashMap<String, String> orderBookMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{ put("pair", pair); put("limit", limit); }
        };
        JSONObject ob = new JSONObject(this.Request("order_book", orderBookMap));
        
        String bid_top = ob.getJSONObject("BTC_USD").getString("bid_top");
        String ask_top = ob.getJSONObject("BTC_USD").getString("ask_top");
        double ask = Double.parseDouble(ask_top);
        double bid = Double.parseDouble(bid_top);
        System.out.println("--------------------------");
        System.out.println("Currency trade prices:");
        System.out.print(" BTC -> USD = ");
        System.out.println((ask+bid)/2);
        return ob;
    }
    
    public JSONObject getLast100Trades(String pair) {
    	HashMap<String, String> tradesMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 2L;
			{ put("pair", pair); put(null, null); }
        };
        JSONObject t = new JSONObject(this.Request("trades", tradesMap));
        
        JSONArray BTCtrades = t.getJSONArray("BTC_USD");
        String price0 = (new JSONObject(BTCtrades.get(0).toString())).getString("price");
        double high = Double.parseDouble(price0);
        double low = high;
        for (int i = 1; i < 100; i++) {
        	String priceN = (new JSONObject(BTCtrades.get(i).toString())).getString("price");
        	if (Double.parseDouble(priceN) > high) high = Double.parseDouble(priceN);
        	if (Double.parseDouble(priceN) < low) low = Double.parseDouble(priceN);
        }
        System.out.println(" High = " + String.valueOf(high));
        System.out.println(" Low = " + String.valueOf(low));
        return t;
    }
    
	private static String unixSecondsToString(String unixSecondsString) {
		long unixSeconds = Long.parseLong(unixSecondsString);
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT-3")); 
	    String formattedDate = sdf.format(date);
	    return formattedDate;
	}

	private static String hexToAscii(String hexStr) {
	    StringBuilder output = new StringBuilder("");
	    for (int i = 0; i < hexStr.length(); i += 2) {
	        String str = hexStr.substring(i, i + 2);
	        output.append((char) Integer.parseInt(str, 16));
	    }
	    return output.toString();
	}
	
    private static Stream<String> readFile(String stringPath) throws IOException {
    	Path path = FileSystems.getDefault().getPath(stringPath);
        Stream<String> stream = Files.lines(path);
        return stream;
    }

    public final String Request(String method, Map<String, String> arguments) {
        Mac mac;
        SecretKeySpec key;
        String sign;

        if (arguments == null) {  // If the user provided no arguments, just create an empty argument array.
            arguments = new HashMap<>();
        }

        arguments.put("nonce", "" + ++_nonce);  // Add the dummy nonce.

        String postData = "";

        for (Map.Entry<String, String> stringStringEntry : arguments.entrySet()) {
            @SuppressWarnings("rawtypes")
			Map.Entry argument = (Map.Entry) stringStringEntry;

            if (postData.length() > 0) {
                postData += "&";
            }
            postData += argument.getKey() + "=" + argument.getValue();
        }

        // Create a new secret key
        try {
            key = new SecretKeySpec(_secret.getBytes("UTF-8"), "HmacSHA512");
        } catch (UnsupportedEncodingException uee) {
            System.err.println("Unsupported encoding exception: " + uee.toString());
            return null;
        }

        // Create a new mac
        try {
            mac = Mac.getInstance("HmacSHA512");
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("No such algorithm exception: " + nsae.toString());
            return null;
        }

        // Init mac with key.
        try {
            mac.init(key);
        } catch (InvalidKeyException ike) {
            System.err.println("Invalid key exception: " + ike.toString());
            return null;
        }


        // Encode the post data by the secret and encode the result as base64.
        try {
            sign = Hex.encodeHexString(mac.doFinal(postData.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException uee) {
            System.err.println("Unsupported encoding exception: " + uee.toString());
            return null;
        }

        // Now do the actual request
        MediaType form = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        try {

            RequestBody body = RequestBody.create(form, postData);
            Request request = new Request.Builder()
                    .url("https://api.exmo.com/v1/" + method)
                    .addHeader("Key", _key)
                    .addHeader("Sign", sign)
                    .post(body)
                    .build();
            
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            System.err.println("Request fail: " + e.toString());
            return null;  // An error occured...
        }
    }
}
