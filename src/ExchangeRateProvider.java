import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

class ExchangeRateProvider {
    private final Map<String, Double> ratesToUsd;
    private static final String API_URL = "https://open.er-api.com/v6/latest/USD";

    public ExchangeRateProvider() {
        ratesToUsd = new HashMap<>();
        fetchLiveRates();
    }

    private void fetchLiveRates() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            System.out.println("[System] Fetching live currency matrices from global exchange api...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                parseJsonRates(response.body());
                System.out.println("[System] Dynamic exchange data downloaded successfully.");
            } else {
                System.out.println("[Warning] Remote API responded with status: " + response.statusCode());
                useFallbackRates();
            }
        } catch (Exception e) {
            System.out.println("[Warning] Network connectivity failed. Injecting safe system fallback definitions.");
            useFallbackRates();
        }
    }

    // A lightweight, manual JSON string parser targeting the "rates":{...} object block
    private void parseJsonRates(String jsonResponse) {
        int ratesIndex = jsonResponse.indexOf("\"rates\":");
        if (ratesIndex == -1) {
            useFallbackRates();
            return;
        }

        // Clean out outer JSON metadata to isolate currency key-value pairs
        String ratesBlock = jsonResponse.substring(ratesIndex);
        ratesBlock = ratesBlock.substring(ratesBlock.indexOf("{") + 1, ratesBlock.indexOf("}"));
        String[] pairs = ratesBlock.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String currencyCode = keyValue[0].replace("\"", "").trim().toUpperCase();
                double dynamicRate = Double.parseDouble(keyValue[1].trim());
                ratesToUsd.put(currencyCode, dynamicRate);
            }
        }
    }

    private void useFallbackRates() {
        ratesToUsd.put("USD", 1.0);
        ratesToUsd.put("EUR", 0.92);
        ratesToUsd.put("GBP", 0.79);
        ratesToUsd.put("RWF", 1300.0);
        ratesToUsd.put("JPY", 155.0);
    }

    public boolean isCurrencySupported(String code) {
        return !ratesToUsd.containsKey(code.toUpperCase());
    }

    public double convert(String fromCurrency, String toCurrency, double amount) {
        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();
        double amountInUsd = amount / ratesToUsd.get(from);
        return amountInUsd * ratesToUsd.get(to);
    }
}
