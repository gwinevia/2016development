/*
	第3回C課題(3)
	2つの変数の値を入れ替える関数を作成しなさい．
	作成した関数をmail関数から呼び出し，入れ替え前と入れ替え後のデータを表示しなさい．
*/
#include <stdio.h>

void swap(int *iNum1, int *iNum2);

int main(void)
{
	int iNum1,iNum2;

	printf("Num1を入力してください:");
	scanf("%d",&iNum1);

	printf("Num2を入力してください:");
	scanf("%d",&iNum2);

	printf("入れ替え前\nNum1 = %d\nNum2 = %d\n",iNum1,iNum2);

	swap(&iNum1,&iNum2);

	printf("入れ替え後\nNum1 = %d\nNum2 = %d\n",iNum1,iNum2);


	return 0;
}

/*
	2つの変数の値を入れ替える関数
	引数は，Num1(int型)，Num2(int型)
	戻り値なし
*/
void swap(int *iNum1, int *iNum2)
{
	int iTmp;

	iTmp = *iNum1;
	*iNum1 = *iNum2; 
	*iNum2 = iTmp; 

}