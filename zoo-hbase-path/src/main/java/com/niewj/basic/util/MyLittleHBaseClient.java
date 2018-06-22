package com.niewj.basic.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

// Put, Get ,Scan 针对一个hbase表.  API 基于HBase 1.0.
public class MyLittleHBaseClient {
    public static void main(String[] args) throws IOException {

        String tableNameString = "zoo_htable_1";

        Configuration config = HBaseConfiguration.create();

        // 从zk配置加载hbase信息
        config.set("hbase.zookeeper.quorum", "master.14.niewj.spark,slave1.146.niewj.spark,slave2.207.niewj.spark");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("zookeeper.znode.parent", "/hbase");

        // 1. 接下来，您需要连接到群集。 创建一个。 当它完成后， 关闭它。
        // 2. try / finally是确保它被关闭的一个好方法(或使用jdk7，try-with-resources)
        // 3. Connection是重量级的。创建一个并保持它。
        // 4. 从Connection中，可取Table实例以访问Tables，管理集群的Admin实例以及RegionLocator，以查找集群上的区域。
        // 5. 与Connections相反，Table，Admin和RegionLocator实例是轻量级的; 根据需要创建，然后在完成后关闭。
        Connection connection = ConnectionFactory.createConnection(config);
        try {

            // 1. 下面实例化一个Table对象，它将您连接到“zoo_htable_1”表（TableName.valueOf将String转换为TableName实例）。
            // 2. 当它完成后，关闭它（应该开始一个尝试/终于在这个创建后，所以它被关闭的肯定
            Table table = connection.getTable(TableName.valueOf(tableNameString));
            try {

                // 1. 要添加到行中，请使用Put。 Put构造函数将要插入的行的名称作为字节数组。
                // 在HBase中，Bytes类具有将各种java类型转换为字节数组的实用工具。
                // 在下面，我们将字符串“myLittleRow”转换成一个字节数组作为我们更新的rowkey。
                // 一旦你有了一个Put实例，你可以设置行上更新的column的名称，使用的时间戳等: 如果没有时间戳，服务器将当前时间用于编辑。
                Put p = new Put(Bytes.toBytes("rk_100001"));

                // 在“rk_100001”行设置要更新的值，请指定单元格的列族、列名和值。
                // 该列族必须已经存在。列明可以是任何东西。 所有值必须为字节数组，因为hbase全部是关于字节数组的。
                // 让我们假装表myLittleHBaseTable是用 cf1 系列创建的
                p.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("uname"), Bytes.toBytes("乔布斯"));

                // 所有更新在Put实例中，提交它使用 HTable#put 推送到hbase
                table.put(p);

                // 现在，检索刚刚写入的数据。 返回的值是Result实例。 结果是一个hbase返回最可口形式的对象
                Get g = new Get(Bytes.toBytes("rk_100001"));
                Result r = table.get(g);
                byte[] value = r.getValue(Bytes.toBytes("cf1"), Bytes.toBytes("uname"));

                // 将字节值转换，返回插入的实际值
                String valueStr = Bytes.toString(value);
                System.out.println("\t GET: " + valueStr);

                // 有时候，你不知道你要找的那一行. 在本例中，您使用了一个Scanner。为表内容提供类似指针的接口。 设置 Scanner, 就像组装 Put 和 Get一样创建一个Scan. 用column names装饰.
                Scan s = new Scan();
                s.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("uname"));
                ResultScanner scanner = table.getScanner(s);
                try {
                    // Scanners 返回 Result 实例. 迭代结果方法一：
                    for (Result result = scanner.next(); result != null; result = scanner.next()) {
                        // 打印出查得的 columns内容
                        System.out.println("Found row: " + result);
                    }

                    // 另一种方法是使用foreach循环。scanner是iterable
                    // for (Result rr : scanner) {
                    //   System.out.println("Found row: " + rr);
                    // }
                } finally {
                    // 确保使用完后关闭 scanners! 所以放入finally:
                    scanner.close();
                }

                // 关闭table、关闭connection
            } finally {
                if (table != null) {
                    table.close();
                }
            }
        } finally {
            connection.close();
        }
    }
}