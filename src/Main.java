import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;


public class Main extends BaseClass {
    
    public static void main(String[] args) throws JSONException, ParseException, IOException, InterruptedException {
    	// Global Variables
        exmo = new Exmo();
        BTC = new BTC_USD();
        BCH = new BCH_USD();
        ETH = new ETH_USD();
        LTC = new LTC_USD();
        
        Long hora;
        String data;
        int i = 0;
        //waitFor(ONEHOUR);
        while(true) {
        	hora = currentHour();
        	data = "";
            
            if (i == 0) {
            	setCurrentPrices("BTC_USD,BCH_USD,ETH_USD,LTC_USD");
                System.out.println("Hora " + hora);
            	System.out.println("BTC: Open=" + String.valueOf(BTC.open));
                System.out.println("BCH: Open=" + String.valueOf(BCH.open));
                System.out.println("ETH: Open=" + String.valueOf(ETH.open));
                System.out.println("LTC: Open=" + String.valueOf(LTC.open) + "\n");
            }
            
            if (i > 0) {
               	setLowHighValues("BTC_USD,BCH_USD,ETH_USD,LTC_USD");
                System.out.println("BTC: Open=" + String.valueOf(BTC.open) + " Low=" + String.valueOf(BTC.low) + " High=" + String.valueOf(BTC.high));
                System.out.println("BCH: Open=" + String.valueOf(BCH.open) + " Low=" + String.valueOf(BCH.low) + " High=" + String.valueOf(BCH.high));
                System.out.println("ETH: Open=" + String.valueOf(ETH.open) + " Low=" + String.valueOf(ETH.low) + " High=" + String.valueOf(ETH.high));
                System.out.println("LTC: Open=" + String.valueOf(LTC.open) + " Low=" + String.valueOf(LTC.low) + " High=" + String.valueOf(LTC.high) + "\n");
            }

            waitFor(ONEMINUTE);
            i++;
            
            if (i == 60) {
            	i = 0;
            	setClosePrices("BTC_USD,BCH_USD,ETH_USD,LTC_USD");
            	data = data.concat("%TimeStamp=" + String.valueOf(hora) + ",BTC:Open=" + String.valueOf(BTC.open) + ",BTC:Close=" + String.valueOf(BTC.close) + ",BTC:Low=" + String.valueOf(BTC.low) + ",BTC:High=" + String.valueOf(BTC.high));
            	data = data.concat("%TimeStamp=" + String.valueOf(hora) + ",BCH:Open=" + String.valueOf(BCH.open) + ",BCH:Close=" + String.valueOf(BCH.close) + ",BCH:Low=" + String.valueOf(BCH.low) + ",BCH:High=" + String.valueOf(BCH.high));
            	data = data.concat("%TimeStamp=" + String.valueOf(hora) + ",ETH:Open=" + String.valueOf(ETH.open) + ",ETH:Close=" + String.valueOf(ETH.close) + ",ETH:Low=" + String.valueOf(ETH.low) + ",ETH:High=" + String.valueOf(ETH.high));
            	data = data.concat("%TimeStamp=" + String.valueOf(hora) + ",LTC:Open=" + String.valueOf(LTC.open) + ",LTC:Close=" + String.valueOf(LTC.close) + ",LTC:Low=" + String.valueOf(LTC.low) + ",LTC:High=" + String.valueOf(LTC.high));
            	
            	writeUsingFiles(data);
            }
        }

    }
}
