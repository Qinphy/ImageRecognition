package com.qinphy.recognition.repository;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * @author: Qinphy
 * @Description: This is HBase's basic methods.
 * @date: 2020/6/23 16:40
 */
public class HBase {
    private static Configuration conf;
    private static Connection conn;
    private static Admin admin;

    public HBase() {
        conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://Master:9000/hbase");
        try {
            conn = ConnectionFactory.createConnection(conf);
            admin = conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (admin != null) {
                admin.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断表是否存在
     * @param tableName 表名称
     * @return true/false 是否存在
     * @throws IOException
     */
    public boolean isTableExist(String tableName) throws IOException {
        TableName table = TableName.valueOf(tableName);
        if (admin.tableExists(table)) return true;
        return false;
    }

    /**
     * 创建表
     * @param tableName 表名称
     * @param cols 列族名称
     * @throws IOException
     */
    public void create(String tableName, String[] cols) throws IOException {
        TableName table = TableName.valueOf(tableName);
        HTableDescriptor hTableDescriptor = new HTableDescriptor(table);
        for (int i = 0; i < cols.length; i++) {
            HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(Bytes.toBytes(cols[i]));
            hTableDescriptor.addFamily(hColumnDescriptor);
        }
        admin.createTable(hTableDescriptor);
    }

    /**
     * 插入一条byte[]类型的数据
     * @param tableName 表名
     * @param rowKey 行关键字
     * @param colFamily 列族名称
     * @param value 插入的值
     * @throws IOException
     */
    public void insert(String tableName, String rowKey, String colFamily, byte[] value) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Put put = new Put(rowKey.getBytes());
        String colName = "";
        put.addColumn(colFamily.getBytes(), colName.getBytes(), value);
        table.put(put);
        table.close();
    }

    /**
     * 获取单元格的值
     * @param tableName 表名称
     * @param rowKey 行关键字
     * @param colFamily 列族名称
     * @return 单元格的值
     * @throws IOException
     */
    public byte[] getCell(String tableName, String rowKey, String colFamily) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Get get = new Get(rowKey.getBytes());
        if(!get.isCheckExistenceOnly()) {
            get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(""));
            Result result = table.get(get);
            byte[] data = result.getValue(Bytes.toBytes(colFamily), Bytes.toBytes(""));
            return data;
        } else return null;
    }

    /**
     * 删除单元格
     * @param tableName 表名称
     * @param rowKey 行关键字
     * @param colFamily 列族名称
     * @throws IOException
     */
    public void delete(String tableName, String rowKey, String colFamily) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(rowKey.getBytes());
        delete.addFamily(colFamily.getBytes());
        table.delete(delete);
        table.close();
    }

    /**
     * 清空表
     * @param tableName 表名称
     * @throws IOException
     */
    public void truncate(String tableName) throws IOException {
        TableName table = TableName.valueOf(tableName);
        admin.disableTable(table);
        admin.truncateTable(table, true);
    }
}