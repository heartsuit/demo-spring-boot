package demo;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * @Author Heartsuit
 * @Date 2020-09-22
 */
public class ShardingDemo {
    public static void main(String[] args) {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置第 1 个数据源
        DruidDataSource druidDataSource1 = new DruidDataSource();
        druidDataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource1.setUrl("jdbc:mysql://localhost:3306/sharding-sphere1?serverTimezone=UTC");
        druidDataSource1.setUsername("root");
        druidDataSource1.setPassword("root");
        dataSourceMap.put("ds1", druidDataSource1);

        // 配置第 2 个数据源
        DruidDataSource druidDataSource2 = new DruidDataSource();
        druidDataSource2.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource2.setUrl("jdbc:mysql://localhost:3306/sharding-sphere2?serverTimezone=UTC");
        druidDataSource2.setUsername("root");
        druidDataSource2.setPassword("root");
        dataSourceMap.put("ds2", druidDataSource2);

        // 配置 t_order 表规则
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration("t_order", "ds${1..2}.t_order_${1..2}");

        // 配置分库策略
//        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("customer_id", "ds${customer_id % 2 + 1}"));
        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("customer_id", "ds${customer_id % 2 + 1}"));

        // 配置分表策略
//        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("id", "t_order_${id % 2 + 1}"));
        orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("id", "t_order_${id % 2 + 1}"));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);

        // 配置分库算法
//        Properties dbShardingAlgorithmrProps = new Properties();
//        dbShardingAlgorithmrProps.setProperty("algorithm-expression", "ds${user_id % 2}");
//        shardingRuleConfig.getShardingAlgorithms().put("dbShardingAlgorithm", new ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmrProps));

        // 配置分表算法
//        Properties tableShardingAlgorithmrProps = new Properties();
//        tableShardingAlgorithmrProps.setProperty("algorithm-expression", "t_order${order_id % 2}");
//        shardingRuleConfig.getShardingAlgorithms().put("tableShardingAlgorithm", new ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmrProps));

        // 创建 ShardingSphereDataSource
//        DataSource dataSource = ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, Collections.singleton((shardingRuleConfig), new Properties());

        try {
            System.out.println("Connection starting...");
            DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new Properties());
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("insert into t_order values(?, ?, ?, ?)");

            System.out.println("Start writing...");
            for (int i = 11; i <= 20; i++) {
                ps.setInt(1, i);
                ps.setInt(2, i);
                ps.setInt(3, new Random().nextInt(10));
                ps.setDouble(4, i * 10.0);
                ps.execute();
                System.out.println(i + " written");
            }
            System.out.println("Connection is going down...");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
