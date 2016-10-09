/*
	第3回C課題(5)
	int型配列のデータをまとめて初期化する関数を作成
	作成した関数をmain関数から呼び出す
*/
#include <stdio.h>

void InitArray(int iArray[],int iNum,int iElement);

int main(void)
{
	int iArray[10] = {3,2,10,9,4,6,8,1,7,5};
	int iLoopCnt = 0;
	int iNum,iElement;

	printf("変更後の値:");
	scanf("%d",&iNum);

	printf("変更する要素数:");
	scanf("%d",&iElement);

	//配列のデータをまとめて初期化する
	InitArray(iArray,iNum,iElement);

	//結果を表示する
	for(iLoopCnt=0;iLoopCnt<10;iLoopCnt++)
	{
		printf("iArray[%d] = %d\n",iLoopCnt,iArray[iLoopCnt]);
	}

	return 0;
}

/*
	int型配列のデータをまとめて初期化する関数
	引数は配列(int []型)，変更後の値(int型)，変更する要素数(int型)
	戻り値なし
*/
void InitArray(int iArray[],int iNum,int iElement)
{
	int iLoopCnt = 0;

	for(iLoopCnt=0;iLoopCnt<iElement;iLoopCnt++)
	{
		iArray[iLoopCnt] = iNum;
	}

}