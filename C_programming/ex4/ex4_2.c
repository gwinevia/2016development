//練習問題2
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int dt = 100;

	//通常の加算式
	dt = dt + 1;
	printf("結果1 = %d\n", dt);
	
	//前置型インクリメント
	dt++;
	printf("結果2 = %d\n", dt);
	
	//後置型インクリメント
	++dt;
	printf("結果3 = %d\n", dt);
	
	//複合代入加算
	dt += 1;
	printf("結果4 = %d\n", dt);

	return 0;
}