# Understanding the WordCount.java Code

There are three main aspects to this code
1. Mapper Class
2. Reducer Class
3. Driver/Main method


# Mapper Class
Let's start with the first class, Mapper class

Mapper Class is responsible for Map phase -> Break the input into pieces and do some processing (like counting words)
Apache Hadoop provides a Generic Mapper class that takes some input and gives some output

`Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>`

```
KEYIN -> Input Key Type -> Object
VALUEIN -> Input Value Type -> Text
KEYOUT -> Output key (that Mapper emits) -> Text
VALUEOUT -> Output value (that Mapper emits) -> IntWritable
```


**Note:**
Hadoop uses special types like:
- Text instead of String
- IntWritable instead of int
Because it needs to serialize (send and store) data efficiently in a distributed system.

Each line is split into words, and for every word, the mapper outputs:
```
("hello", 1)
("world", 1)
("hello", 1)
```

Hadoop gives the input to the mapper automatically through something called an InputFormat.
It splits your input file line by line, Then it gives each line to the Mapper

keyin -> the byte offset (position) of the line in the file → not very useful to us here
valuein -> the line of text itself

```java
public static class TokenizerMapper
  extends Mapper<Object, Text, Text, IntWritable> { ... }
```

<table border="1">
  <thead>
    <tr>
      <th>Type</th>
      <th>What it means</th>
      <th>In this example</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>KEYIN</td>
      <td>Input key</td>
      <td>Object → file offset, e.g., 0L, 21L</td>
    </tr>
    <tr>
      <td>VALUEIN</td>
      <td>Input value</td>
      <td>Text → a line from the file, like "hello world"</td>
    </tr>
    <tr>
      <td>KEYOUT</td>
      <td>Output key</td>
      <td>Text → the word, like "hello"</td>
    </tr>
    <tr>
      <td>VALUEOUT</td>
      <td>Output value</td>
      <td>IntWritable → always 1 (one word occurrence)</td>
    </tr>
  </tbody>
</table>

## Now let us take a look at the code

```java
public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
    // Now when hadoop calls the map function, we want to map each word with (word, 1)
    // As previously mentioned, hadoop uses special datatype like IntWritable instead of int
    // So, since we are always going to set the value of word to one, let us create
    // a reusable object of IntWriter that has value 1
    private static final IntWritable one = new IntWritable(1);

    // Similar to IntWriter, Hadoop's version of String is Text
    // Let us create a variable to hold each word we find in the line
    private Text word = new Text();

    // This is the main method that Hadoop calls automatically for every line of the
    // input file.
    // public void map(Object key, Text value, Context context)
    // key is the byte offset in the file (we don’t care about it here)
    // value is the actual line of text from the file (like "hello hadoop")
    // context is used to send output (like: ("hello", 1))
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // Convert the special Text datatype to String. Example: "hello hadoop"
        String str_value = value.toString();

        // Tokenize the string. itr = ["hello", "hadoop"]
        StringTokenizer itr = new StringTokenizer(str_value);

        while (itr.hasMoreTokens()) {
            // set the current word
            word.set(itr.nextToken());
            // Output (word, 1)
            context.write(word, one);
        }

        /*
            Example:
            Input Line: hello world hello
            Step 1: Convert it into String -> "hello word hello"
            Step 2: Tokenize the String -> ["hello","word","hello"]
            Step 3: For each word, output (word, 1)
            Final Output:
            ("hello", 1)
            ("world", 1)
            ("hello", 1)
        */
    }
}
```


# Reducer Class

Syntax
```java
public static class IntSumReducer
  extends Reducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
```

<table border="1">
  <thead>
    <tr>
      <th>Part</th>
      <th>What it means</th>
      <th>In this example</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>KEYIN</td>
      <td>The input key type</td>
      <td>Text → the word (like "hello")</td>
    </tr>
    <tr>
      <td>VALUEIN</td>
      <td>The input values (grouped by key)</td>
      <td>IntWritable → all the 1s from Mapper</td>
    </tr>
    <tr>
      <td>KEYOUT</td>
      <td>The output key type (what Reducer emits)</td>
      <td>Text → the word again</td>
    </tr>
    <tr>
      <td>VALUEOUT</td>
      <td>The output value type (final count per word)</td>
      <td>IntWritable → total count of that word</td>
    </tr>
  </tbody>
</table>

**From the Mapper, we get:**
```
("hello", 1)
("world", 1)
("hello", 1)
```
**Before reaching the Reducer, Hadoop groups these by key:**
```
"hello" → [1, 1]
"world" → [1]
```

**So the Reducer receives:**
KEYIN = "hello"
VALUEIN = [1, 1]
Final output from Reducer:
After summing values, it outputs:
("hello", 2)
("world", 1)

## Let us look at the Reducer code
```java
public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    // We'll create a variable where we store the final count of each word.
    private IntWritable result = new IntWritable();

    // reduce method
    // This is the main method that Hadoop calls.
    // key is a word like "hello"
    // values is a list of all the 1s associated with that word, like [1, 1, 1]
    // context is used to write the final output
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        result.set(sum);
        context.write(key, result);

        /*
        Input to Reducer
        hello : [1,1]
        
        Output:
        (hello, 2)
        */
    }
}
```

# Main method

Main method does everything needed to:
- Parse arguments
- Set up the job
- Assign Mapper, Reducer, etc.
- Provide input/output paths
- Submit the job and wait for results

## Let us look at the Main method
```java
public static void main(String[] args) throws Exception {
    // Load your Hadoop settings from core-site.xml, hdfs-site.xml, etc.
    // This can be done using Hadoop Configuration object
    Configuration conf = new Configuration();

    // Parse the Command Line Argument using GenericOptionsParser
    // It parses command-line arguments (args) like: hadoop jar wordcount.jar
    // input.txt output
    // The getRemainingArgs() function strips out any Hadoop-specific options and
    // leaves only: input path and output path
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    // You must pass at least: One input file and One output directory
    if (otherArgs.length < 2) {
        System.err.println("Usage: wordcount <in> [<in>...] <out>");
        System.exit(2);
    }

    // Now create a Hadoop Map Reduce Job
    // Creates a new MapReduce job instance.
    // "word count" is just a name to identify the job.
    Job job = Job.getInstance(conf, "word count");

    // Tell Hadoop where the main class is located (for packaging).
    job.setJarByClass(WordCount.class);

    // Tell Hadoop where the Mapper class is located
    job.setMapperClass(TokenizerMapper.class);

    // Tell Hadoop where the Reducer class is located
    job.setReducerClass(IntSumReducer.class);

    /*
        * Optional -> setCombinerClass -> tell Hadoop to run a mini-reducer before the
        * actual Reducer.
        * Helps in network traffic
        * In this program, it is safe to use it.
        * Why? Notice how in reducer class, we doing sum += val.get() instead of just
        * sum += 1
        * The reason is, if a mini reducer runs before the actual reducer, it running
        * mini reducer before, may give the input to reducer as
        * "hello" : [1,2,1,4]
        * so we are summing as 1 + 2 + 1 + 4 = 8
        * Instead of just 1 + 1 + 1 + 1 = 4
        */
    job.setCombinerClass(IntSumReducer.class);

    // Set the output key type from Reducer
    job.setOutputKeyClass(Text.class);
    // Set the output value type from Reducer
    job.setOutputValueClass(IntWritable.class);

    // Set all input file paths except the last one (which is for output).
    for (int i = 0; i < otherArgs.length - 1; ++i) {
        FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
    }

    // Sets the output folder where final result (like part-r-00000) will go.
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[otherArgs.length - 1]));

    // Submit the job to Hadoop.
    // Wait for it to complete.
    // Returns exit code: 0 -> success, 1 -> failure
    System.exit(job.waitForCompletion(true) ? 0 : 1);
}
```