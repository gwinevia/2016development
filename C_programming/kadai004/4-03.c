//第4回C言語課題(3)
//構造体employee型にメンバーを追加し,BMI値,肥満率,理想体重を計算する
#include <stdio.h>
#include <string.h>

struct employee{
	int number;     //社員番号
	char name[20];  //名前
	double height;  //身長	
	double weight;  //体重
  double bmi;     //BMI値
  double fat;     //肥満率 
  double ideal;   //理想体重 

};

void bmi_func(struct employee *employees);
int main(void)
{
	struct employee employees;       //構造体employee型の変数
  struct employee *p = &employees; //構造体employeeのポインタ宣言

  //初期化
  employees.number = 1;
  strcpy(employees.name,"oohata");
  employees.height = 173.0;
  employees.weight = 62.0;
  employees.bmi = 0.0;
  employees.fat = 0.0;
  employees.ideal = 0.0;


  //BMI値,肥満率,理想体重を計算する
  bmi_func(p);

  //結果出力
  printf("社員番号:%d\n名前:%s\n身長:%.1f\n体重:%.1f\nBMI値:%.1f\n肥満率:%.1f\n理想体重:%.1f\n"
  	,employees.number,employees.name,employees.height,employees.weight,employees.bmi,employees.fat,employees.ideal);

	return 0;
}

//BMI値,肥満率,理想体重を計算する関数
void bmi_func(struct employee *employees)
{
  //BMI = 体重(kg)/身長(m)^2
  employees->bmi = employees->weight / (employees->height * employees->height / 10000);

  //理想体重 = (身長(cm)-100)*0.9 
  employees->ideal = (employees->height - 100) * 0.9;

  //肥満率 = (体重(kg)/理想体重(kg))*100
  employees->fat = (employees->weight / employees->ideal) * 100;
   
}