//第4回C言語課題(1)
//構造体employee型の作成
#include <stdio.h>
#include <string.h>

struct employee{
	int number;     //社員番号
	char name[20];  //名前
	double height;  //身長	
	double weight;  //体重
};

int main(void)
{
	struct employee employees; //構造体employee型の変数

  //初期化
  employees.number = 1;
  strcpy(employees.name,"oohata");
  employees.height = 173.0;
  employees.weight = 62.0;

  //結果出力
  printf("社員番号:%d\n名前:%s\n身長:%.1f\n体重:%.1f\n"
  	,employees.number,employees.name,employees.height,employees.weight);

	return 0;
}