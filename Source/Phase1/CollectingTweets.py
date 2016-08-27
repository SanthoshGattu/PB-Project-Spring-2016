from tweepy import Stream
from tweepy import OAuthHandler
from tweepy.streaming import StreamListener
#import time 
ckey =  'oeNeYCoPc1CZ4yyUIWNhoGADa'
csecret = 'DzqW7aQACQE69FKKbkwOEaGFGoAP4zpXuBoN9IvX2JZGNfAMM3'
atoken =  '2351892476-dkvDlPZ6iLp0jtct4jhVeADho8qUIGn88CDJCr6'
asecret =  'agCjnjdhoYA0WRZOsQBQVdbtfC9rPv9h32XDktFY7Ruyd'
class listener(StreamListener):
	def on_data(self,data):
			print (data)
			saveFile = open('WT20Tweets.json' ,'a')
			saveFile.write(data)
			saveFile.write('\n')
			saveFile.close()
			return True
	def on_error(self, status):
		print (status)
auth = OAuthHandler(ckey, csecret)
auth.set_access_token(atoken, asecret)
twitterStream = Stream(auth,listener())
twitterStream.filter(track = ["WT20"])
