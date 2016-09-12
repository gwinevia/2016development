/*
	第2回C課題(5)
	ｘのｎ乗を計算する関数を作り，メイン関数から呼び出し
	算出結果を表示するプログラム
*/
#include <stdio.h>

int func_fact(int iNum_X,int iNum_N);

int main(void)
{
	int iLoopCnt = 0;
	int iNum_X,iNum_N;
	int iResult = 0;  //関数func_fact()からの戻り値を格納

	printf("ｘのｎ乗を計算します\n");
	
	printf("ｘを入力してください：");
	scanf("%d",&iNum_X);

	printf("ｎを入力してください：");
	scanf("%d",&iNum_N);


	iResult = func_fact(iNum_X,iNum_N);
	printf("算出結果(%dの%d乗) = %d\n",iNum_X,iNum_N,iResult);

	return 0;
}

/*
	int型の整数ｘ，ｎを引数として受け取り，
	ｘのｎ乗を求める関数．戻り値は算出結果．
*/
int func_fact(int iNum_X,int iNum_N)
{
	int iPower = 1; //ｘのｎ乗の算出結果を格納．初期値は1．
	int iLoopCnt = 0;

	for(iLoopCnt=0;iLoopCnt<iNum_N;iLoopCnt++)
	{
		iPower *= iNum_X;
	}
	
	return iPower;
}