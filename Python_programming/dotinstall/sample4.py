# coding:UTF-8

#-----------------------------------------------
# for ループ
users = {"taguchi":200, "fkoji":300, "dot":500}
for key, value in users.iteritems():
		print "key: %s value: %d" % (key, value)

for key in users.iterkeys():
		print key

for value in users.itervalues():
		print value

#-----------------------------------------------
# while ループ
n = 0
while n < 10:
		print n
		n = n + 1 # n += 1
else:
		print "end"

n = 0
while n < 10:
		if n == 3:
			n += 1
			continue
		print n
		n = n + 1 # n += 1
else:
		print "end"

n = 0
while n < 10:
		if n == 3:
			break
		print n
		n = n + 1 # n += 1
else:
		print "end" # "end"も表示されない