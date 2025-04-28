import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import java.util.*;
import java.io.IOException;

public class WordCount {
	
	public static class TokenMapper extends Mapper<Object, Text, Text, IntWritable>{
		public static final IntWritable one = new IntWritable(1);
		public final Text word = new Text();
		public void map(Object key, Text line, Context context) throws IOException, InterruptedException {
			String strLine = line.toString();
			StringTokenizer tokenizer = new StringTokenizer(strLine);
			while(tokenizer.hasMoreTokens()) {
				word.set(tokenizer.nextToken());
				context.write(word, one);
			}
		}
		
	}
	
	
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		public final IntWritable result = new IntWritable();
		public void reduce(Text word, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for(IntWritable value : values) {
				sum += value.get();
			}
			result.set(sum);
			context.write(word, result);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setMapperClass(TokenMapper.class);
		job.setReducerClass(IntSumReducer.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setJarByClass(WordCount.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		String[] otherArgs = new GenericOptionsParser(args).getRemainingArgs();
		
		for(int i=0; i<otherArgs.length-1; i++) {
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[otherArgs.length - 1]));
		
		job.waitForCompletion(true);
	}
	
	
}
