package com.qinphy.recognition.repository;

import com.qinphy.recognition.entity.Bmp;
import com.qinphy.recognition.entity.Split;
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
import org.apache.hadoop.io.*;
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
    // 需要搜索的图像
    private static Bmp bmp;

    // AllSearch参数
    private static final String tableName = "imageMR";
    private static String colFamily;
    private static final String col = "";
    private static byte[] imgCounter;

    // PartSearch参数
    private static int sum = 0;
    private static byte[] img;
    private static int splitWidth;
    private static int splitHeight;
    private static byte leftTop;
    private static byte rightTop;
    private static byte middle;
    private static byte leftBottom;
    private static byte rightBottom;

    // VagueSearch参数
    private static byte[] datas;

    private static class AllMap extends TableMapper<Text, NullWritable> {

        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

            // 遍历表格的每一个单元
            for(Cell cell : value.rawCells()){
                // 找到对应的单元
                if (colFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)))
                        && col.equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    byte[] b = CellUtil.cloneValue(cell);

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
                        context.write(new Text(name), NullWritable.get());
                    }
                }
            }
        }
    }

    private static class AllReduce extends Reducer<Text, NullWritable, Text, NullWritable> {

        @Override
        public void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, NullWritable.get());
        }
    }

    private static class PartMap extends TableMapper<ImmutableBytesWritable, Text> {

        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

            // String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
            // 遍历表格的每一个单元
            for(Cell cell : value.rawCells()){
                // 找到对应的单元
                if (colFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)))
                        && col.equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    byte[] b = CellUtil.cloneValue(cell);
                    List<Split> list = Change.split(b, 512, 512, splitWidth, splitHeight, leftTop, rightTop, middle, leftBottom, rightBottom);

                    String name = Change.changeToString(CellUtil.cloneRow(cell));
                    for (int i = 0; i < list.size(); i++) {
                        Split s = list.get(i);
                        if (s.getSum() == sum) context.write(new ImmutableBytesWritable(s.getData()), new Text(name));
                    }
                }
            }
        }
    }

    private static class PartReduce extends Reducer<ImmutableBytesWritable, Text, Text, NullWritable> {

        @Override
        public void reduce(ImmutableBytesWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            byte[] data = key.get();
            for(int i = 0; i < data.length; i++) {
                if (data[i] != img[i]) return;
            }
            for (Text text: values) {
                String fileName = text.toString();
                System.out.println(fileName);
                context.write(text, NullWritable.get());
            }
        }
    }

    private static class VagueMap extends TableMapper<IntWritable, Text> {

        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

            for(Cell cell : value.rawCells()){
                // 找到对应的单元
                if (colFamily.equals(Bytes.toString(CellUtil.cloneFamily(cell)))
                        && col.equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {

                    byte[] b = CellUtil.cloneValue(cell);
                    int fenmu = 0;
                    int fenzi = 0;
                    for (int i = 0; i < b.length; i++) {
                        if (b[i] == datas[i]) fenzi++;
                        fenmu++;
                    }
                    int percent = fenzi * 100 / fenmu;
                    String name = Change.changeToString(CellUtil.cloneRow(cell));

                    context.write(new IntWritable(percent), new Text(name));
                }
            }
        }
    }

    private static class VagueReduce extends Reducer<IntWritable, Text, IntWritable, Text> {

        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text text: values) {
                context.write(key, text);
            }
        }
    }

    public static String AllSearch(Bmp myBmp) throws IOException, ClassNotFoundException, InterruptedException {
        bmp = myBmp;
        colFamily = "counter";
        int[] img = bmp.getCounter();
        imgCounter = Change.changeToByte(img);

        Job job = connect();
        List<Scan> list = getList();

        TableMapReduceUtil.initTableMapperJob(list, AllMap.class, ImmutableBytesWritable.class, ImmutableBytesWritable.class, job);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setReducerClass(AllReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        String hdfsPath = "/user/qinphy/output/all";
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
        splitWidth = bmp.getWidth();
        splitHeight = bmp.getHeight();
        img = Change.changeToByte(bmp.getData());

        int splitWidth2 = splitWidth * 4;
        leftTop = img[0];
        rightTop = img[splitWidth2 - 1];
        middle = img[splitWidth2 * (splitHeight / 2 - 1) + splitWidth2 / 2 - 1];
        leftBottom = img[splitWidth2 * (splitHeight - 1)];
        rightBottom = img[img.length - 1];

        byte[] bmpData = Change.changeToByte(bmp.getData());
        int bmpSum = 0;
        for (int i = 0; i < bmpData.length; i++) {
            bmpSum += bmpData[i];
        }
        sum = bmpSum;

        Job job = connect();
        List<Scan> list = getList();

        TableMapReduceUtil.initTableMapperJob(list, PartMap.class, ImmutableBytesWritable.class, ImmutableBytesWritable.class, job);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(PartReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        String path = "/user/qinphy/output/part";
        Path jPath = new Path(path);
        FileOutputFormat.setOutputPath(job, jPath);
        MultipleOutputs.addNamedOutput(job, "hdfs", TextOutputFormat.class, WritableComparable.class, Writable.class);
        int ex = job.waitForCompletion(true) == true ? 0 : 1;
        System.out.println(ex);
        return path;
    }

    public static String VagueSearch(Bmp myBmp) throws IOException, ClassNotFoundException, InterruptedException {
        bmp = myBmp;
        colFamily = "image";
        int[][] data = bmp.getData();
        datas = Change.changeToByte(data);

        Job job = connect();
        List<Scan> list = getList();

        TableMapReduceUtil.initTableMapperJob(list, VagueMap.class, ImmutableBytesWritable.class, ImmutableBytesWritable.class, job);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(VagueReduce.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        String hdfsPath = "/user/qinphy/output/vague";
        Path path = new Path(hdfsPath);
        FileOutputFormat.setOutputPath(job, path);
        MultipleOutputs.addNamedOutput(job, "hdfs", TextOutputFormat.class, WritableComparable.class, Writable.class);

        job.waitForCompletion(true);
        return hdfsPath;
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
