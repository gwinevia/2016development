/*
	第2回C課題(2)
	サイズ100の配列内の各要素に1〜100の数値を代入し，
	その総和を求めるプログラム
*/
#include <stdio.h>
int main(void)
{
	int iLoopCnt = 0;
	int iSum = 0;     //1〜100の数値の総和を格納
	int iArray[100];

	for(iLoopCnt=0;iLoopCnt<100;iLoopCnt++)
	{
		//サイズ100の配列内の各要素に1〜100の数値を代入
		iArray[iLoopCnt] = iLoopCnt + 1;

		//1〜100の数値の総和を求める
		iSum += iArray[iLoopCnt];
	}

	printf("1〜100の数値の総和 = %d\n",iSum);

	return 0;
}