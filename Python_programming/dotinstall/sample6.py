# coding:UTF-8

#-----------------------------------------------
# リスト <> 関数 map
def double(x):
		return x * x

print map(double, [2,5,8])

# 無名関数
print map(lambda x:x * x, [2,5,8])


#-----------------------------------------------
# オブジェクト（変数と関数をまとめたもの）
# クラス:オブジェクトの設計図
# インスタンス:クラスを実体化したもの
class User(object):
		def __init__(self, name):
				self.name = name
		def greet(self):
				print "my name is %s" % self.name

bob = User("Bob")
tom = User("Tom")
print bob.name
bob.greet()
tom.greet()

#-----------------------------------------------
# クラスの継承
class SuperUser(User):
		def shout(self):
				print "%s is SUPER!!" % self.name

bob = User("Bob")
tom = SuperUser("Tom")
bob.greet()
tom.shout()

#-----------------------------------------------
# モジュール
import math, random
from datetime import date

print math.ceil(5.2)

for i in range(5):
		print random.random()

print date.today()