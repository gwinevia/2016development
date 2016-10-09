/*
	第3回C課題(1)
	サイズ5のint型配列を定義し，その配列の全要素のアドレスを
	表示させるプログラム
*/
#include <stdio.h>

int main(void)
{
	int iLoopCnt = 0;
	int iArray[5];  //サイズ5のint型配列

	for(iLoopCnt=0;iLoopCnt<5;iLoopCnt++)
	{
		printf("%p\n",&iArray[iLoopCnt]);
	}

	return 0;
}