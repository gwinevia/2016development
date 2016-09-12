/*
	第2回C課題(4)
	4つの整数の足し算結果を求める関数を作り，メイン関数から呼び出し，
	算出結果を表示するプログラム
*/
#include <stdio.h>

int func_sum(int iNum1,int iNum2,int iNum3,int iNum4);

int main(void)
{
	int iLoopCnt = 0;
	int iArray[4];    //4つの整数を格納
	int iResult = 0;  //関数func_sum()からの戻り値を格納

	while(iLoopCnt<4)
	{
		printf("整数を入力：");
		scanf("%d",&iArray[iLoopCnt]);
		iLoopCnt++;
	}

	iResult = func_sum(iArray[0],iArray[1],iArray[2],iArray[3]);
	printf("算出結果 = %d\n",iResult);

	return 0;
}

/*
	int型の整数を4つ引数として受け取り，
	その整数の足し算結果を求める関数．戻り値は算出結果．
*/
int func_sum(int iNum1,int iNum2,int iNum3,int iNum4)
{
	int iSum = 0; //整数の足し算結果を格納

	iSum = iNum1 + iNum2 + iNum3 + iNum4;

	return iSum;
}