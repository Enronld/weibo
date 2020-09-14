package com.springboot.hbase.weibo.dao;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HbaseMapper {

    public static Configuration config = null;
    public static Connection connection = null;
    public Table table = null;
    public static Admin admin;
    public static Table table1;
    public static Table table2;


    //初始化连接
    public static void init() throws Exception {
        config = HBaseConfiguration.create();

        config.set("hbase.zookeeper.quorum","master");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        connection = ConnectionFactory.createConnection(config);
        admin = connection.getAdmin();
        System.out.println(admin);
    }

    //根据rowkey、列簇和列获取某一行数据
    public static HashMap<String, String> getCell(String tableName,String rowKey, String family, String col) throws Exception {

        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(rowKey.getBytes());
        get.addColumn(Bytes.toBytes(family),Bytes.toBytes(col));
        HashMap<String,String> row = new HashMap<>();
        Result res = table.get(get);
        Cell[] cells = res.rawCells();
        if (cells.length != 0){
            for (Cell cell : cells) {
                    row.put("rowKey",Bytes.toString(CellUtil.cloneRow(cell)));
                    row.put("columnFamily",Bytes.toString(CellUtil.cloneFamily(cell)));
                    row.put("column",Bytes.toString(CellUtil.cloneQualifier(cell)));
                    row.put("value",Bytes.toString(CellUtil.cloneValue(cell)));
            }
            return row;
        }
        return null;
    }

    //根据rowkey、列簇获获取数据
    public static List<HashMap<String,String>> getColumnFamilyCell(String tableName,String rowKey, String family) throws Exception {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(rowKey.getBytes());
        get.addFamily(Bytes.toBytes(family));
        List<HashMap<String,String>>  user = new ArrayList<>();
        Result res = table.get(get);
        Cell[] cells = res.rawCells();
        if (cells.length != 0){
            for (Cell cell : cells) {
                HashMap<String,String> data = new HashMap<>();
                data.put("rowKey",Bytes.toString(CellUtil.cloneRow(cell)));
                data.put("columnFamily",Bytes.toString(CellUtil.cloneFamily(cell)));
                data.put("column",Bytes.toString(CellUtil.cloneQualifier(cell)));
                data.put("value",Bytes.toString(CellUtil.cloneValue(cell)));
                user.add(data);
            }
            return user;
        }
        return null;
    }

    //插入一条数据
    public static void put(String tableName, String row, String columnFamily, String column, String data)throws Exception {
        init();
        TableName tablename = TableName.valueOf(tableName);
        Table table = connection.getTable(tablename);
        Put p = new Put(Bytes.toBytes(row));
        p.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(data));
        table.put(p);
        System.out.println("put '" + row + "','" + columnFamily + ":" + column + "','" + data + "'");
    }

    //根据列簇和列获取表中所有数据
    public static List<HashMap<String,String>> scanAll(String tableName,String columnFamily,String column) throws Exception {
        init();
        TableName tablename = TableName.valueOf(tableName);
        Table table = connection.getTable(tablename);
        Scan s = new Scan();
        ResultScanner rs = table.getScanner(s);
        List<HashMap<String,String>> all = new ArrayList<>();
        for (Result result : rs) {
            Cell[] cells = result.rawCells();
            int i = 0;
            for (Cell cell : cells) {
                if (Bytes.toString(CellUtil.cloneFamily(cell)).equals(columnFamily)&&Bytes.toString(CellUtil.cloneQualifier(cell)).equals(column)){
                    HashMap<String,String> data = new HashMap<>();
                    data.put("rowKey",Bytes.toString(CellUtil.cloneRow(cell)));
                    data.put("columnFamily",Bytes.toString(CellUtil.cloneFamily(cell)));
                    data.put("column",Bytes.toString(CellUtil.cloneQualifier(cell)));
                    data.put("value",Bytes.toString(CellUtil.cloneValue(cell)));
                    all.add(data);
                    i++;
                }

            }

        }
        return all;
    }

    //根据列簇获取表中所有数据
    public static List<HashMap<String,String>> scanAll2(String tableName,String columnFamily) throws Exception {
        init();
        TableName tablename = TableName.valueOf(tableName);
        Table table = connection.getTable(tablename);
        Scan s = new Scan();
        ResultScanner rs = table.getScanner(s);
        List<HashMap<String,String>> all = new ArrayList<>();
        for (Result result : rs) {
            Cell[] cells = result.rawCells();
            int i = 0;
            for (Cell cell : cells) {
                if (Bytes.toString(CellUtil.cloneFamily(cell)).equals(columnFamily)){
                    HashMap<String,String> data = new HashMap<>();
                    data.put("rowKey",Bytes.toString(CellUtil.cloneRow(cell)));
                    data.put("columnFamily",Bytes.toString(CellUtil.cloneFamily(cell)));
                    data.put("column",Bytes.toString(CellUtil.cloneQualifier(cell)));
                    data.put("value",Bytes.toString(CellUtil.cloneValue(cell)));
                    all.add(data);
                    i++;
                }

            }

        }
        return all;
    }

    //删除指定列
    public static void deleteCellData(String tableName, String rowKey,String family, String col) throws IOException {

        TableName tablename = TableName.valueOf(tableName);
        Table table = connection.getTable(tablename);

        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addColumns(Bytes.toBytes(family), Bytes.toBytes(col));
        table.delete(delete);

    }



}
