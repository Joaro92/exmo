import java.util.List;

public class Trading extends BaseClass {

	public void trading() throws InterruptedException {
		// formula: EMA[hoy] = (Precio * K) + (EMA[ayer] * (1 - K)
		double fastEMA;
		double slowEMA;
		while(true) {
			waitFor(TENMINUTES);
			fastEMA = calculateEMA(8);
			slowEMA = calculateEMA(22);
			if (fastEMA > slowEMA * 1.003) {
				// Comprar, si tengo dolares....
			}
			if (fastEMA < slowEMA * 0.999) {
				// Vender, si tengo moneda....
			}
		}
	}
	
	public Double calculateEMA(int length) {
		double k = 2 / (length + 1);
		List<Double> prices = getPrices(length);
		double result = (prices.get(0) * k) + average(prices);
		for (int i = 1; i < length; i++) {
			result = (prices.get(i) * k) + (result * (1 - k));
		}
		
		return result;
	}
	
	public List<Double> getPrices(long period) {
		return null;
	}
	
	public Double average(List<Double> prices) {
		double result = 0;
		for (Double p : prices) {
			result += p;
		}
		return result / prices.size();
	}
}
