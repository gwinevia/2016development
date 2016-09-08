/*
	第2回C課題(3)
	サイズ10の配列にscanf関数でそれぞれの数値を入力し，
	その総和と平均を求めるプログラム
*/
#include <stdio.h>
int main(void)
{
	int iLoopCnt = 0;
	int iSum = 0;        //数値の総和を格納
	double iAverage = 0; //数値の平均を格納
	int iArray[10];

	for(iLoopCnt=0;iLoopCnt<10;iLoopCnt++)
	{
		//サイズ10の配列にscanf関数でそれぞれの数値を入力
		printf("数字を入力してください:");
		scanf("%d",&iArray[iLoopCnt]);

		//数値の総和を求める
		iSum += iArray[iLoopCnt];
	}

	//数値の平均を求める
	iAverage = (double)iSum / 10;

	printf("総和 = %d\n平均 = %.2f\n",iSum,iAverage);

	return 0;
}