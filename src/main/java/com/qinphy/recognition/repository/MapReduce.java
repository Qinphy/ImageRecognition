package com.qinphy.recognition.repository;

import com.qinphy.recognition.entity.Bmp;
import com.qinphy.recognition.util.Change;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Qinphy
 * @Description: MapReduce所有的相关方法
 * @date: 2020/6/26 9:51
 */
public class MapReduce {
    private static Bmp bmp;
    private static final String tableName = "imageMR";
    private static String colFamily;
    private static final String col = "";


    private static class AllMap extends TableMapper<Text, Text> {

        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

            // 遍历表格的每一个单元
            for(Cell cell : value.rawCells()){
                // 找到对应的单元
                if (colFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)))
                        && col.equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    byte[] b = CellUtil.cloneValue(cell);
                    int[] img = bmp.getCounter();
                    byte[] imgCounter = Change.changeToByte(img);

                    boolean f = true;
                    for (int i = 0; i < b.length; i++) {
                        if (b[i] != imgCounter[i]) {
                            f = false;
                            break;
                        }
                    }

                    if (f) {
                        byte[] a = CellUtil.cloneRow(cell);
                        String name = Change.changeToString(a);
                        context.write(new Text(""), new Text(name));
                    }
                }
            }
        }
    }

    private static class AllReduce extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text text: values) {
                context.write(new Text(""), text);
            }
        }
    }

    private static class PartMap extends TableMapper<ImmutableBytesWritable, ImmutableBytesWritable> {

        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

            // 遍历表格的每一个单元
            for(Cell cell : value.rawCells()) {
                // 找到对应的单元
                if (colFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)))
                        && col.equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    byte[] b = CellUtil.cloneValue(cell);
                    // 拆分图片,需要优化
                    List<byte[]> list = Change.split(b, 512, 512, bmp.getWidth(), bmp.getHeight());

                    // 拆分后的part作为一个map
                    for (int i = 0; i < list.size(); i++) {

                        byte[] img = list.get(i);
                        ImmutableBytesWritable part = new ImmutableBytesWritable(img);

                        byte[] a = CellUtil.cloneRow(cell);
                        ImmutableBytesWritable rowKey = new ImmutableBytesWritable(a);

                        context.write(part, rowKey);
                    }

                }
            }
        }
    }

    private static class PartReduce extends Reducer<ImmutableBytesWritable, ImmutableBytesWritable, Text, Text> {

        @Override
        public void reduce(ImmutableBytesWritable key, Iterable<ImmutableBytesWritable> values, Context context) throws IOException, InterruptedException {
            for (ImmutableBytesWritable value: values) {
                byte[] b = key.get();
                byte[] img = Change.changeToByte(bmp.getData());

                boolean f = true;
                for (int i = 0; i < b.length; i++) {
                    if (b[i] != img[i]) {
                        f = false;
                        break;
                    }
                }

                if (f) context.write(new Text(""), new Text(Bytes.toString(value.get())));
            }
        }
    }

    public static String AllSearch(Bmp myBmp) throws IOException, ClassNotFoundException, InterruptedException {
        bmp = myBmp;
        colFamily = "counter";

        Job job = connect();
        List<Scan> list = getList();

        TableMapReduceUtil.initTableMapperJob(list, AllMap.class, ImmutableBytesWritable.class, ImmutableBytesWritable.class, job);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(AllReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        String hdfsPath = "/user/qinphy/all";
        Path path = new Path(hdfsPath);
        FileOutputFormat.setOutputPath(job, path);
        MultipleOutputs.addNamedOutput(job, "hdfs", TextOutputFormat.class, WritableComparable.class, Writable.class);

        int ex = job.waitForCompletion(true) == true ? 0 : 1;
        System.out.println("exit = " + ex);
        return hdfsPath;
    }

    public static String PartSearch(Bmp myBmp) throws IOException, ClassNotFoundException, InterruptedException {
        bmp = myBmp;
        colFamily = "image";

        Job job = connect();
        List<Scan> list = getList();

        TableMapReduceUtil.initTableMapperJob(list, PartMap.class, ImmutableBytesWritable.class, ImmutableBytesWritable.class, job);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(ImmutableBytesWritable.class);
        job.setReducerClass(PartReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        String path = "/user/qinphy/part";
        Path jPath = new Path(path);
        FileOutputFormat.setOutputPath(job, jPath);
        MultipleOutputs.addNamedOutput(job, "hdfs", TextOutputFormat.class, WritableComparable.class, Writable.class);
        int ex = job.waitForCompletion(true) == true ? 0 : 1;
        System.out.println(ex);
        return path;
    }

    private static Job connect() throws IOException {
        Configuration conf  = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.137.120:9000");
        conf.set("hbase.zookeeper.quorum", "Master,Worker1,Worker2");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        Job job = Job.getInstance(conf,"SearchPart");
        job.setJarByClass(MapReduce.class);
        return job;
    }

    private static List<Scan> getList() {
        List<Scan> list = new ArrayList<Scan>();
        Scan scan = new Scan();
        scan.setCaching(2000);
        scan.setCacheBlocks(false);
        scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, tableName.getBytes());
        list.add(scan);
        return list;
    }
}
