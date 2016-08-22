// 第1回課題(2)
/*	
	整数値を1つ入力し，その値が2の倍数，3の倍数，
 	2と3の公倍数，それ以外の4パターンで異なる結果を
 	出力するプログラム
*/
#include <stdio.h>
int main(int argc, char const *argv[])
{
	int iNum = 0;

	printf("整数値を1つ入力してください:");
	scanf("%d",&iNum);

  /*	
  	2で割った余りが0かつ3で割った余りが0：2と3の公倍数
		2で割った余りが0：2の倍数
		3で割った余りが0：3の倍数
	*/
	if(iNum%2==0 && iNum%3==0 && iNum!=0){ 
	 	printf("%dは2と3の公倍数です\n",iNum);
	}else if(iNum%2==0 && iNum!=0){
	 	printf("%dは2の倍数です\n",iNum);
	}else if(iNum%3==0 && iNum!=0){
	 	printf("%dは3の倍数です\n",iNum);
	}else{ // その他
	 	printf("%dは2の倍数でも3の倍数でもありません\n",iNum);
	}
	
	return 0;
}