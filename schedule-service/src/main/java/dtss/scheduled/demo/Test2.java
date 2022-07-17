package dtss.scheduled.demo;

import dtss.scheduled.db.DruidUtil;
import lombok.SneakyThrows;

import java.sql.Connection;

public class Test2 {
    @SneakyThrows
    public static void main(String[] args) {
//close 掉了 就会归还到连接池 可复用
        Connection connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();
        connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();
        connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();       connection = DruidUtil.getConnection();
        System.out.println(connection);
        connection.close();

    }
}
