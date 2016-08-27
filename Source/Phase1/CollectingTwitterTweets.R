library(streamR)
library(ROAuth)
#create your OAuth credential
credential <- OAuthFactory$new(consumerKey='oeNeYCoPc1CZ4yyUIWNhoGADa',
                               consumerSecret='DzqW7aQACQE69FKKbkwOEaGFGoAP4zpXuBoN9IvX2JZGNfAMM3',
                               requestURL='https://api.twitter.com/oauth/request_token',
                               accessURL='https://api.twitter.com/oauth/access_token',
                               authURL='https://api.twitter.com/oauth/authorize')

#authentication process
options(RCurlOptions = list(cainfo = system.file("CurlSSL", "cacert.pem", package = "RCurl")))
download.file(url="http://curl.haxx.se/ca/cacert.pem", destfile="cacert.pem")
credential$handshake(cainfo="cacert.pem")

#function to actually scrape Twitter
filterStream( file.name="tweets_test.json",
              track="IPLAuction", tweets=1000, oauth=credential, timeout=10, lang='en' )
#Parses the tweets
tweet_df <- parseTweets(tweets='tweets_test.json')

#using the Twitter data frame
tweet_df$created_at
tweet_df$text


plot(tweet_df$friends_count, tweet_df$followers_count) #plots scatterplot
cor(tweet_df$friends_count, tweet_df$followers_count) #returns the correlation coefficient