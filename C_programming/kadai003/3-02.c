/*
	第3回C課題(2)
	身長と体重からBMI値，肥満度，理想体重を算出する関数を作りなさい．
	また，作成した関数をmain関数から呼び出しなさい．
*/
#include <stdio.h>

void bmi_fat(int iHeight, int iWeight, double *dBMI, double *dObesity, double *dIdeal);

int main(void)
{
	int iHeight,iWeight;      //入力された身長と体重の値を格納
	double dBMI,dObesity,dIdeal;  //BMI値，肥満度，理想体重の値を格納

	printf("身長(cm)を入力してください:");
	scanf("%d",&iHeight);

	printf("体重(kg)を入力してください:");
	scanf("%d",&iWeight);

	bmi_fat(iHeight,iWeight,&dBMI,&dObesity,&dIdeal);

	printf("BMI値 = %.2f\n", dBMI);
	printf("肥満度 = %.2f(％)\n", dObesity);
	printf("理想体重 = %.2f(kg)\n", dIdeal);


	return 0;
}

/*
	身長と体重からBMI値，肥満度，理想体重を算出する関数
	引数は，身長(int型)，体重(int型)，BMI値(ポインタ型)，肥満度(ポインタ型)，理想体重(ポインタ型)
	戻り値なし
*/
void bmi_fat(int iHeight, int iWeight, double *dBMI, double *dObesity, double *dIdeal)
{
	double dHeight; //身長(m)の値を格納

	dHeight = iHeight / 100.0;

	//BMI値を算出
	*dBMI = iWeight / (dHeight * dHeight);

	//理想体重を算出
	*dIdeal = (iHeight - 100) * 0.9;

	//肥満度を算出
	*dObesity = ((iWeight - *dIdeal) / *dIdeal) * 100;
}