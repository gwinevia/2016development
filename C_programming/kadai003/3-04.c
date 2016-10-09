/*
	第3回C課題(4)
	サイズ10のint型配列に昇順なソートをかけて表示するプログラム
*/
#include <stdio.h>

void sort(int iArray[]);

int main(void)
{
	int iArray[10] = {3,2,10,9,4,6,8,1,7,5};
	int iLoopCnt = 0;

	//昇順にソートする
	sort(iArray);

	//結果を表示する
	for(iLoopCnt=0;iLoopCnt<10;iLoopCnt++)
	{
		printf("iArray[%d] = %d\n",iLoopCnt,iArray[iLoopCnt]);
	}

	return 0;
}

/*
	引数は配列(int []型)
	戻り値なし
*/
void sort(int iArray[])
{
	int iTemp;
	int iLoopCnt = 0;
	int iArraySize = 10; //配列のサイズを格納

	while(iArraySize > 2)
	{
		for(iLoopCnt=0;iLoopCnt<iArraySize-1;iLoopCnt++)
		{
			//左から順に隣合わせの2つのデータの大小を比較し左のデータの方が大きい時に交換する
			if(iArray[iLoopCnt] > iArray[iLoopCnt+1])
			{
				iTemp = iArray[iLoopCnt+1];
				iArray[iLoopCnt+1] = iArray[iLoopCnt];
				iArray[iLoopCnt] = iTemp;
			}
		}
		iArraySize--;
	}
}