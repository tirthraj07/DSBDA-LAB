import spark.implicits._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

// singleton instance
object WordCount {

    def main(args: Array[String]):Unit = {

        // Start a Spark Session -> name cores create
        val spark = SparkSession.builder.appName("word count").master("local[*]").getOrCreate()
        
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