//第4回C言語課題(2)
//構造体employee型にメンバーを追加し,BMI値を計算し,表示する
#include <stdio.h>
#include <string.h>

struct employee{
	int number;     //社員番号
	char name[20];  //名前
	double height;  //身長	
	double weight;  //体重
  double bmi;     //BMI値 
};

double bmi_func(struct employee employees);
int main(void)
{
	struct employee employees; //構造体employee型の変数

  //初期化
  employees.number = 1;
  strcpy(employees.name,"oohata");
  employees.height = 173.0;
  employees.weight = 62.0;
  employees.bmi = 0.0;

  //BMI値を求める
  employees.bmi = bmi_func(employees);

  //結果出力
  printf("社員番号:%d\n名前:%s\n身長:%.1f\n体重:%.1f\nBMI値:%.1f\n"
  	,employees.number,employees.name,employees.height,employees.weight,employees.bmi);

	return 0;
}

//BMI値を求める関数
double bmi_func(struct employee employees)
{
  double dBMI = 0.0;

  //BMI = 体重(kg)/身長(m)^2
  dBMI = employees.weight / (employees.height * employees.height / 10000);
   
  return dBMI;
}