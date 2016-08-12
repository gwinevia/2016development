// 第1回課題(5)
// 九九の表を表示するプログラム
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int iLoopCnt,jLoopCnt;

	for(iLoopCnt=1;iLoopCnt<=9;iLoopCnt++){
		for(jLoopCnt=1;jLoopCnt<=9;jLoopCnt++){
			printf("%d*%d= %d\t",iLoopCnt,jLoopCnt,iLoopCnt*jLoopCnt);
		}
		putchar('\n');
	}

	return 0;
}