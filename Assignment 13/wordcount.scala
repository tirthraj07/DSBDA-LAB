import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

// singleton instance
object WordCount {

    def main(args: Array[String]):Unit = {

        // Start a Spark Session -> name cores create
        val spark = SparkSession.builder.appName("word count").master("local[*]").getOrCreate()

        // Import the implicits_ after spark session is initialized
        import spark.implicits._

        // Streaming <- Load in lines
        val lines = spark.readStream.format("socket").option("host","localhost").option("port",9999).load()

        // Mapper
        var words = lines.as[String].flatMap(line => line.split(" "))

        // Reducer
        var count = words.groupBy("value").count()

        // Output -> START STREAMING COMPLETE OUTPUT ON CONSOLE
        var query = count.writeStream.outputMode("complete").format("console").start()

        query.awaitTermination()
    }
}

/*
    Step 1:    Open primary terminal and type spark-shell
    Step 2:    Inside of the scala repl (scala >) Paste this code
    Step 3:    Now open another terminal and type : nc -lk 9999
    Step 4:    Comeback to primary terminal and type : WordCount.main(Array())
    Step 5:    Once the program has started, go back to secondary terminal and type a sentence and hit enter
    Step 6:    Back in primary terminal, you should see the list of words with their respective counts
*/