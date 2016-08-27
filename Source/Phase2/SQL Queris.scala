/**
  * Created by Santhosh on 3/26/2016.
  */

//import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{FloatType, DateType}
import org.apache.spark.sql.functions._

object Tweets {
  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir", "c:\\winutil");

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc=new SparkContext(sparkConf)

    // Contains SQLContext which is necessary to execute SQL queries
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    // Reads json file and stores in a variable
    val tweet = sqlContext.read.json("C:\\Users\\Santhosh\\Desktop\\WT20Tweets.json")

    //To register tweets data as a table
    tweet.registerTempTable("tweets")

    // To print tweets schema
    //tweets.printSchema()

    //To show the data collected for each tweet
   // tweet.collect()

    //saved to cache for improving perfomance
    sqlContext.cacheTable("tweets")

    //Query 1: Distribution of tweets across 10 different time zones and respective user language
    val loclang = sqlContext.sql("""SELECT user.time_zone, user.lang, COUNT(text)cnt from tweets
                                    GROUP BY user.time_zone, user.lang
                                    ORDER BY cnt desc limit 10""")

    loclang.collect().foreach(println)

    //loclang.write.format("com.databricks.spark.csv").option("header", "true").save("C:\\Users\\Santhosh\\Desktop\\SqlOP1.csv")


    //Query 2:  Tweets generated per hour
    val tweetsperhour = sqlContext.sql(""" SELECT a.date, count(text) as cnt FROM
                                      ( SELECT text, SUBSTRING(created_at,0,13) AS date
                                        FROM tweets where text!= '')a
                                        GROUP BY a.date ORDER BY cnt desc""")

    tweetsperhour.collect().foreach(println)

    //tweetsperhour.write.format("com.databricks.spark.csv").option("header", "true").save("C:\\Users\\Santhosh\\Desktop\\SqlOP2.csv")


    //Query 3: Most famous person using retweeted tweets count
    val famous = sqlContext.sql("""SELECT a.name, a.location, count(a.text) AS total_tweets, sum(a.retweets) AS total_retweets
                                    FROM (SELECT
                                          retweeted_status.user.screen_name as name,
                                          retweeted_status.user.location as location,
                                          retweeted_status.text as text,
                                          max(retweeted_status.retweet_count) as retweets
                                          FROM tweets
                                          GROUP BY retweeted_status.user.screen_name,retweeted_status.user.location,retweeted_status.text)a
                                          GROUP BY a.name, a.location
                                          ORDER BY total_retweets DESC LIMIT 10 """)
    famous.collect().foreach(println)

    //famous.write.format("com.databricks.spark.csv").option("header", "true").save("C:\\Users\\Santhosh\\Desktop\\SqlOP3.csv")
	

//Query 4 : Percentage of tweets based on each language

    val lang = sqlContext.sql("SELECT user.lang, COUNT(text) cnt FROM tweets GROUP BY user.lang ORDER BY cnt DESC limit 10")
    val total_tweets =  sqlContext.sql("SELECT text FROM tweets WHERE text != '' ")
    val alltweetscount = total_tweets.count()
    val percentage = lang.withColumn("percentage",(lang("cnt") * 100)/alltweetscount)
    percentage.registerTempTable("percentage")
    val  allpercentage= sqlContext.sql("SELECT * FROM percentage ORDER BY cnt DESC")
    val langpercentage = allpercentage.select("lang","percentage")
    langpercentage.collect().foreach(println)

    //Query 5 : Maximum Followers count for top 10 users in descending order

    val followers = sqlContext.sql("""SELECT user.screen_name, max(user.followers_count) from tweets
                                     where user.screen_name is not null GROUP BY user.screen_name
                                     ORDER BY max(user.followers_count) desc limit 10""")

    followers.collect().foreach(println)

  }
}
