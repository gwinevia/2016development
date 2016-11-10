# coding:UTF-8

#-----------------------------------------------
# セット（集合型） - 重複を許さない
a = set([1, 2, 3, 4])
b = set([3, 4, 5])

print a

# -
print a - b

# |
print a | b

# &
print a & b  

# ^
print a ^ b


#-----------------------------------------------
# 辞書 key value 
sales = {"taguchi":200, "tanaka":300, "dot":500}
print sales
print sales["taguchi"]

sales["tanaka"] = 800
print sales

# in
print "tanaka" in sales  

# keys, values, items
print sales.keys()
print sales.values()
print sales.items()


#-----------------------------------------------
s = 10
t = 1.234234
u = "taguchi"
v = {"fkoji":200, "dot":500}

print "age: %d" % s
print "age: %10d" % s
print "age: %010d" % s

print "price: %f" % t
print "price: %.2f" % t

print "name: %s" % u

print "sales: %(fkoji)d" %v