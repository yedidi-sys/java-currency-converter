import java.util.Scanner;

class CurrencyApp {
    public static void main(String[] args) {
        ExchangeRateProvider rateProvider = new ExchangeRateProvider();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter a source currency (e.g., USD) or type 'exit' to quit:");
            String from = scanner.next().toUpperCase();
            if (from.equals("EXIT")) break;

            System.out.println("Enter a destination currency (e.g., EUR):");
            String to = scanner.next().toUpperCase();
            if (to.equals("EXIT")) break;

            // Fix: Check if NOT supported (!)
            if (!rateProvider.isCurrencySupported(from) || !rateProvider.isCurrencySupported(to)) {
                System.out.println("One of the currencies entered is not supported. Please try again.");
                continue;
            }

            System.out.println("Enter the amount to be converted:");
            while (!scanner.hasNextDouble()) {
                System.out.println("Please enter a valid number!");
                scanner.next(); // clear invalid input
            }
            double amount = scanner.nextDouble();
            double result = rateProvider.convert(from, to, amount);
            System.out.printf("%.2f %s is %.2f %s%n", amount, from, result, to);
        }

        System.out.println("Goodbye!");
        scanner.close();
    }
}
