import java.util.HashMap;
import java.util.Map;
class ExchangeRateProvider{
    private final Map<String, Double> ratesToUsd ;
    public ExchangeRateProvider(){
        ratesToUsd = new HashMap<>();
        InitializeDefaultRates();
    }
    private void InitializeDefaultRates(){
        ratesToUsd.put("USD", 1.0 );
        ratesToUsd.put("EUR", 0.98 );
        ratesToUsd.put("RWF", 3000.0);
        ratesToUsd.put("GPY", 155.0);
    }

    public boolean isCurrencySupported(String code){
        return ratesToUsd.containsKey(code.toUpperCase());
    }
    public double convert(String fromCurren, String toCurren, Double amount){
        String from = fromCurren.toUpperCase();
        String to = toCurren.toUpperCase();

        double amountToUsd = amount / ratesToUsd.get(from);
        return amountToUsd * ratesToUsd.get(to);
    }


}