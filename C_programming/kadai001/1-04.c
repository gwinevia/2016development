// 第1回課題(4)
// 1から9までの整数値を順番に表示するプログラム
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int iLoopCnt;

	// 1から9までの整数値を順番に表示
	for(iLoopCnt=1;iLoopCnt<=9;iLoopCnt++){
		printf("%d\n",iLoopCnt);
	}

	return 0;
}