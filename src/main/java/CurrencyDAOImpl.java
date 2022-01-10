import java.lang.reflect.Field;
import java.sql.*;

public class CurrencyDAOImpl extends AbstractDAO {
    public CurrencyDAOImpl(Connection conn, String table) {
        super(conn, table);
    }

    public void rateByTheDate(Connection conn, String table, String date) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE DATE BETWEEN '" + date
                    + "T00:00:00.00' AND '" + date + "T23:59:59.999'");
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Currency res = new Currency();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String columnName = md.getColumnName(i);
                    Field f = Currency.class.getDeclaredField(columnName);
                    if (f.getName().equals("date")) {
                        f.setAccessible(true);
                        f.set(res, rs.getObject(columnName).toString());
                        continue;
                    }
                    f.setAccessible(true);
                    f.set(res, rs.getObject(columnName));
                }
                System.out.println("Date: " + res.getDate() + " Sale rate: " + res.getSaleRateNB() + " Purchase rate: " + res.getPurchaseRateNB());
            }
        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    public void averageRateByPeriod(Connection conn, String table, String from, String to) {
        try {
            Statement st = conn.createStatement();
            //check this query (probably u should add time from 00:00 to 23:59)
            ResultSet rs = st.executeQuery("SELECT AVG(saleRate), AVG(purchaseRate) FROM " + table +
                    " WHERE DATE BETWEEN '" + from + "T00:00:00.00' AND '" + to + "T23:59:59.999'");
            ResultSetMetaData md = rs.getMetaData();
            String purchaseRate;
            String saleRate;
            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String columnName = md.getColumnName(i).toString();
                    if (i == 1) {
                        saleRate = rs.getObject(columnName).toString();
                        System.out.println("Average sale rate is: " + saleRate);
                    } else {
                        purchaseRate = rs.getObject(columnName).toString();
                        System.out.println("Average purchase rate is: " + purchaseRate);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
