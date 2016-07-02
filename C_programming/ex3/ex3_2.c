//練習問題2
#include <stdio.h>
int main(int argc, char const *argv[])
{
	char s[80] = "ABCDE";

	printf("文字列は = %s\n", s);
	printf("最初の文字は = %c\n", s[0]);
	printf("最初の文字コードは = %d\n", s[0]);
	printf("最後の文字は = %c\n", s[4]);
	printf("最後の文字コードは = %d\n", s[4]);

	return 0;
}