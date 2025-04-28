import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object WordCount {

    def main(args: Array[String]) : Unit = {
        // Create Spark Session -> NAME CORES CREATE
        val spark = SparkSession.builder.appName("word count").master("local[*]").getOrCreate()
        
        import spark.implicits._

        // Start loading streaming input
        val lines = spark.readStream.format("socket").option("host","localhost").option("port",9999).load()

        // Mapper function
        val words = lines.as[String].flatMap(line => line.split(" "))

        // Reducer function
        val count = words.groupBy("value").count()

        // Start streaming out on to console
        val query = count.writeStream.outputMode("complete").format("console").start()

        // await for manual termination
        query.awaitTermination()
    }

}

// nc -lk 9999

// WordCount.main(Array())