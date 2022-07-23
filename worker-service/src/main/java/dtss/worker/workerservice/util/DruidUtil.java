package dtss.worker.workerservice.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

// 德鲁伊连接池
public class DruidUtil {
    private static DataSource dataSource;
    static {
        try {
            Properties properties = new Properties();
            properties.load(Files.newInputStream(Paths.get("druid.properties")));
            System.out.println("druid.properties: " + properties);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            System.out.println("读取druid.properties文件异常" + e);
            System.exit(1);
        }
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void close(AutoCloseable... closeables) throws Exception {
        for (AutoCloseable closeable : closeables)
            if (closeable != null)
                closeable.close();
    }





}

