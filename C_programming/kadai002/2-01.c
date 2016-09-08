/*
	第2回C課題①
	1から100までの整数を1行に1つずつ順番に表示するプログラム
	ただし，3の倍数と3のつく数字の時は♪を数字の後ろにつけて表示する
*/
#include <stdio.h>

int func_nabeatsu(int iNum);

int main(void)
{
	int iLoopCnt = 0;
	int iResult = 0;  //関数func_nabeatsu()からの戻り値を格納

	for(iLoopCnt=1;iLoopCnt<=100;iLoopCnt++)
	{
		iResult = func_nabeatsu(iLoopCnt);
		if(iResult == 1)
		{
			printf("%d♪\n",iLoopCnt);
		}
		else
		{
			printf("%d\n",iLoopCnt);
		}
	}

	return 0;
}

/*
	int型の整数を引数として受け取り，その整数が3の倍数，
	3のつく数字，その他の数字を判定する関数．
	戻り値は3の倍数，3のつく数字なら1，その他は0．
*/
int func_nabeatsu(int iNum)
{
	if(iNum%3==0 || iNum%10==3 || iNum/10%10==3 || iNum/100%10==3)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}
