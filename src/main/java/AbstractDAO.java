import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDAO<T> {
    private final Connection conn;
    private final String table;

    public AbstractDAO(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    private Field getPrimaryKey(T t, Field[] fields) {
        Field primaryField = null;
        for (Field f :
                fields) {
            if (f.isAnnotationPresent(Id.class)) {
                primaryField = f;
                primaryField.setAccessible(true);
                break;
            }
            if (primaryField == null) {
                throw new RuntimeException("No id field");
            }
        }
        return primaryField;
    }


    public void createTable(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Field id = getPrimaryKey(null, fields);

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(table).append("(");
        sql.append(id.getName()).append(" ").append("INT AUTO_INCREMENT PRIMARY KEY,");
        for (Field f :
                fields) {
            if (f != id) {
                f.setAccessible(true);
                sql.append(f.getName()).append(" ");

//if problems check date
                if (f.getType() == String.class) {
                    if (f.getName().equalsIgnoreCase("date")) {
                        sql.append("DATE,");
                    } else
                        sql.append("VARCHAR(100),");
                } else {
                    throw new RuntimeException("Wrong type, mate");
                }
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        try {
            try (Statement st = conn.createStatement()) {
                st.execute(sql.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKey(t, fields);

            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for (Field f : fields) {
                if (f != id) {
                    f.setAccessible(true);
                    /*
                    check date also
                     */
                    if(f.getName().equals("date")){
                        String date = (String)f.get(t);
                        String fancyDate = date.substring(6,10) + "-" + date.substring(3,5) + "-" + date.substring(0,2);
                        names.append(f.getName()).append(',');
                        values.append('"').append(fancyDate).append("\",");
                        continue;
                    }

                    names.append(f.getName()).append(',');
                    values.append('"').append(f.get(t)).append("\",");
                }
            }


            names.deleteCharAt(names.length() - 1);
            values.deleteCharAt(values.length() - 1);

            String sql = "INSERT INTO " + table + "(" + names.toString() +
                    ") VALUES(" + values.toString() + ")";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public void update(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKey(t, fields);

            StringBuilder sb = new StringBuilder();

            for (Field f : fields) {
                if (f != id) {
                    f.setAccessible(true);

                    sb.append(f.getName())
                            .append(" = ")
                            .append('"')
                            .append(f.get(t))
                            .append('"')
                            .append(',');
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            String sql = "UPDATE " + table + " SET " + sb.toString() + " WHERE " +
                    id.getName() + " = \"" + id.get(t) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void delete(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getPrimaryKey(t, fields);

            String sql = "DELETE FROM " + table + " WHERE " + id.getName() +
                    " = \"" + id.get(t) + "\"";

            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<T> getAll(Class<T> cls) {
        List<T> res = new ArrayList<>();

        try {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + table)) {
                    ResultSetMetaData md = rs.getMetaData();

                    while (rs.next()) {
                        T t = cls.newInstance();

                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            String columnName = md.getColumnName(i);
                            Field field = cls.getDeclaredField(columnName);
                            field.setAccessible(true);

                            field.set(t, rs.getObject(columnName));
                        }

                        res.add(t);
                    }
                }
            }

            return res;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
