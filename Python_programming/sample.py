# coding:UTF-8

#-----------------------------------------------
# リスト
sales = [255, 100, 353, 400, 'abc']

# len + * []
print len(sales)
print sales[2]
sales[2] = 100
print sales[2]

# in 存在チェック
print 100 in sales

# range
print range(10)
print range(3, 10)
print range(3, 10 ,2)

#-----------------------------------------------
# sort / reverse
sales.sort()
print sales

sales.reverse()
print sales

# 文字列とリスト
d = "2016/11/10"
print d.split("/")

a = ["a", "b", "c"]
print "-".join(a)

#-----------------------------------------------
# タプル（変更ができない）
t = (2, 5, 8)

# len + * []
print len(t)
print t * 3

# t[2]  =10 これは出来ない！

# タプル <> リスト
l = list(t)
print l

c = tuple(l)
print c