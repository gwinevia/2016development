// 第1回課題(1)
// ＊を使って高さ5の三角形を表示するプログラム
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int iLoopCnt1,iLoopCnt2;

	// iLoopCnt  三角形の高さ
	for(iLoopCnt1=1;iLoopCnt1<=5;iLoopCnt1++){
		// jLoopCnt 表示する＊の個数
		for(iLoopCnt2=0;iLoopCnt2<iLoopCnt1;iLoopCnt2++){
			printf("*");
		}
		printf("\n");
	}
	
	return 0;
}