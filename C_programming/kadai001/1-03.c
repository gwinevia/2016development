// 第1回課題(3)
// 1から100までの奇数の和と偶数の和を求めるプログラム
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int iLoopCnt;
	int iSumEven = 0; // 偶数の和を格納する変数
	int iSumOdd = 0;  // 奇数の和を格納する変数
	
	for(iLoopCnt=1;iLoopCnt<=100;iLoopCnt++){
		if(iLoopCnt%2==0) // 2で割った余りが0：偶数
			iSumEven += iLoopCnt;
		else // 2で割った余りが0ではない：奇数
			iSumOdd += iLoopCnt;
	}

	// 求めた1から100までの偶数の和と奇数の和を表示
	printf("1から100までの偶数の和 = %d\n", iSumEven);
	printf("1から100までの奇数の和 = %d\n", iSumOdd);

	return 0;
}