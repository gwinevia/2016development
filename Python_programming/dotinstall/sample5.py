# coding:UTF-8

#-----------------------------------------------
# 関数
def hello():
		print "hello"

hello()


# 引数
def hello(name, num):
		print "hello %s!" % name * num

hello("tom", 2)
hello("steve", 3)


def hello(name, num = 3):
		print "hello %s!" % name * num

hello(name = "tom", num = 2)
hello("steve")


# 返り値
def hello(name, num = 3):
		return "hello %s!" % name * num

s = hello("tom")
print s


#-----------------------------------------------
# 変数のスコープ
name = "taguchi"

def  hello():
		name = "fkoji"

hello()
print name

# pass
def hello2():
		pass

