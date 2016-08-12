// 第1回課題(1)
// ＊を使って高さ5の三角形を表示するプログラム
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int iLoopCnt,jLoopCnt;

	// iLoopCnt  三角形の高さ
	for(iLoopCnt=1;iLoopCnt<=5;iLoopCnt++){
		// jLoopCnt 表示する＊の個数
		for(jLoopCnt=0;jLoopCnt<iLoopCnt;jLoopCnt++){
			printf("*");
		}
		printf("\n");
	}
	
	return 0;
}