// 第1回課題(5)
// 九九の表を表示するプログラム
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int iLoopCnt1,iLoopCnt2;

	for(iLoopCnt1=1;iLoopCnt1<=9;iLoopCnt1++){
		for(iLoopCnt2=1;iLoopCnt2<=9;iLoopCnt2++){
			printf("%d*%d= %d\t",iLoopCnt1,iLoopCnt2,iLoopCnt1*iLoopCnt2);
		}
		putchar('\n');
	}

	return 0;
}