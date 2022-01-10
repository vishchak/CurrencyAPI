import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = ConnectionFactory.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute("DROP TABLE IF EXISTS currency");
            }
            CurrencyDAOImpl table = new CurrencyDAOImpl(conn, "currency");
            table.createTable(Currency.class);

            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            int i = 1;
            //add to the table all the data
            while (!today.equalsIgnoreCase("01.01.2022")) {//there should be 01.12.2014, but it takes forever to load
                Currency cur = DataUSD.usdRateThatDay(today);
                cur.setDate(today);
                table.add(cur);
                today = LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                i++;
            }
            while (true) {
                System.out.println("1: check today's USD rate");
                System.out.println("2: check USD rate by date");
                System.out.println("3: check average USD purchase/sale rate by period");

                String choice = sc.nextLine();
                switch (choice) {
                    case "1":
                        table.rateByTheDate(conn, "currency", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        break;
                    case "2":
                        System.out.println("Enter the date in format yyyy-MM-dd (example: 2022-01-10)");
                        String date = sc.nextLine();
                        table.rateByTheDate(conn, "currency", date);
                        break;
                    case "3":
                        System.out.println("Enter the FIRST date in format yyyy-MM-dd (example: 2022-01-10)");
                        String first = sc.nextLine();
                        System.out.println("Enter the SECOND date in format yyyy-MM-dd (example: 2022-01-10)");
                        String second = sc.nextLine();
                        table.averageRateByPeriod(conn, "currency", first, second);
                    default:
                        return;
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
