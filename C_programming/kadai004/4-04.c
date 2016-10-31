//第4回C言語課題(4)
//構造体employee型の変数を配列で5つ宣言し,BMI値,肥満率,理想体重を求めた後,BMI値の昇順でソートして表示する
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
void sort(struct employee *employees);
int main(void)
{
  //構造体employee型の変数
	struct employee employees[5] = {
              {1,"oohata",173.0,62.0,0.0,0.0,0.0},
              {2,"suzuki",168.0,60.0,0.0,0.0,0.0},
              {3,"satou",186.2,80.5,0.0,0.0,0.0},
              {4,"tanaka",162.5,45.2,0.0,0.0,0.0},
              {5,"yamada",155.3,55.7,0.0,0.0,0.0},              
  };
  
  struct employee *p = employees; //構造体employeeのポインタ宣言

  int iLC; //ループ用変数


  //BMI値,肥満率,理想体重を計算する
  bmi_func(p);

  //BMI値の昇順でソートする
  sort(p);

  //結果出力
  for(iLC=0;iLC<5;iLC++)
  {
    printf("社員番号:%d\n名前:%s\n身長:%.1f\n体重:%.1f\nBMI値:%.1f\n肥満率:%.1f\n理想体重:%.1f\n\n"
  	   ,employees[iLC].number,employees[iLC].name,employees[iLC].height,employees[iLC].weight
       ,employees[iLC].bmi,employees[iLC].fat,employees[iLC].ideal);
  }

	return 0;
}

//BMI値,肥満率,理想体重を計算する関数
void bmi_func(struct employee *employees)
{
  int iLC; //ループ用変数

  for(iLC=0;iLC<5;iLC++)
  {
    //BMI = 体重(kg)/身長(m)^2
    (employees+iLC)->bmi = (employees+iLC)->weight / ((employees+iLC)->height * (employees+iLC)->height / 10000);

    //理想体重 = (身長(cm)-100)*0.9 
    (employees+iLC)->ideal = ((employees+iLC)->height - 100) * 0.9;

    //肥満率 = (体重(kg)/理想体重(kg))*100
    (employees+iLC)->fat = ((employees+iLC)->weight / (employees+iLC)->ideal) * 100;
  }
}

//BMI値の昇順でソートする関数
void sort(struct employee *employees)
{
  struct employee Temp;
  int iLC;
  int iSize = 5; //配列のサイズを格納

  while(iSize > 0)
  {
    for(iLC=0;iLC<iSize-1;iLC++)
    {
      //左から順に隣合わせの2つのデータの大小を比較し左のデータの方が大きい時に交換する
      if((employees+iLC)->bmi > (employees+(iLC+1))->bmi)
      {
        Temp = *(employees+(iLC+1));
        *(employees+(iLC+1)) = *(employees+iLC);
        *(employees+iLC) = Temp;
      }
    }
    iSize--;
  }

}