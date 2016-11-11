# coding:UTF-8

#-----------------------------------------------
# 条件分岐 if
score = 70
if score > 60:
		print "ok!"
		print "OK!"

# 比較演算子 < > <= >= ==
# 論理演算子 and or not
if score > 60 and score < 80:
		print "ok!"
		print "OK!"

score = 45
if score > 60:
		print "ok!"
elif score > 40:
		print "soso..."
else:
		print "ng!"

# 少し特殊な書き方
print "OK!" if score > 60 else "NG!"


#-----------------------------------------------
# for ループ
sales = [13, 3523, 31, 238]
sum = 0
for sale in sales:
		print sale

for sale in sales:
		sum += sale
print sum

for sale in sales:
		sum += sale
else:
		print sum

for i in range(10):
		print i

for i in range(10):
		if i == 3:
				break
		print i