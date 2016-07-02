//練習問題1
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int dt[3] = {100,200,300};
	int sum = 0;

	sum = dt[0] + dt[1] + dt[2];
	printf("合計 = %d\n", sum);

	return 0;
}