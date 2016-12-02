/*

	---第5回C課題①---
	ファイル(Pa2016.txt)は2016年プロ野球パシフィックリーグの対戦成績表
	Pa2016.txtを読み込み、各チームの勝数、負数、引分数、勝率を計算し、
	順位をPa2016result.txtに出力

*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define N 6
#define D_SIZE 37

struct baseball2016
{
	char team_name[10]; // チーム名
	int win;            // 勝利数
	int lose;           // 負数
	int draw;           // 引分数
	double wpct;        // 勝率
} typedef baseball;

void calc_score(int score[N][D_SIZE-1], baseball bb[]);
void sort(baseball bb[]);

int main(void)
{
	baseball bb2016[N];

	FILE *fp; //ファイルポインタの宣言
	char *filename_r = "Pa2016.txt"; //データ読み込み用のファイル
	char *filename_w = "Pa2016result.txt"; //データ書き込み用のファイル
	char s[10];
	int score[N][D_SIZE-1]; //ファイルから読み込んだデータ(数値)を格納

	int iLp = 0;
	int num = 0;

	//データ読み込み用のファイルを開く
	if((fp = fopen(filename_r, "r")) == NULL){
		fprintf(stderr," エラー：ファイル(%s)がオープンできません\n",filename_r);
		return -1;
	}


	//データ読み込み
  while (fscanf(fp,"%s",s) != EOF){
  	if(atoi(s)==0 && strcmp(s,"0")!=0){
    	strcpy(bb2016[num].team_name,s); //チーム名の時は構造体の配列に格納
  	}else{
  		score[num][iLp-1] = atoi(s);
  	}

    iLp++;
    if(iLp == D_SIZE){
    	num++;
    	iLp = 0; //初期値に戻す
    }
  }
	fclose(fp);

	//各チームの勝数、負数、引分数、勝率を計算
	calc_score(score,bb2016);

	//勝率の昇順にソートする
	sort(bb2016);

	//データ書き込み用のファイルを開く
	if ((fp = fopen(filename_w, "w")) == NULL){
		fprintf(stderr," エラー：ファイル(%s)がオープンできません\n",filename_w);
		return -1;
	}

	//結果の書き込み
	for (iLp=0;iLp<N;iLp++){
		fprintf(fp,"%d位：%-10s\t%.3f\n",iLp+1,bb2016[iLp].team_name,bb2016[iLp].wpct);
	}
	fclose(fp);

	return 0;
}

//各チームの勝数、負数、引分数、勝率を計算する関数
void calc_score(int score[N][D_SIZE-1], baseball bb[])
{
	int iLp1,iLp2,iLp3; //ループ用の変数
	int iSum; //勝数、負数、引分数を格納


	for(iLp1=0;iLp1<N;iLp1++){

		//勝数、負数、引分数を計算
		for(iLp2=0;iLp2<3;iLp2++){
			iSum = 0; //初期化
			
			for (iLp3=iLp2;iLp3<D_SIZE-1;iLp3=iLp3+3){
					iSum += score[iLp1][iLp3]; 
			}

			if(iLp2==0){
				bb[iLp1].win = iSum;
			}else if(iLp2 ==1){
				bb[iLp1].lose = iSum;
			}else{
				bb[iLp1].draw = iSum;
			}
		}

		//勝率を計算
		bb[iLp1].wpct = (double)bb[iLp1].win / (bb[iLp1].win + bb[iLp1].lose);
	}
}

//勝率の昇順にソートする
void sort(baseball bb[])
{
	baseball Temp;
	int iSize = N;
	int iLp;

  while(iSize > 0){
  	for(iLp=0;iLp<iSize-1;iLp++){
      if((bb+iLp)->wpct < (bb+(iLp+1))->wpct){
        Temp = *(bb+(iLp+1));
        *(bb+(iLp+1)) = *(bb+iLp);
        *(bb+iLp) = Temp;
      }
  	}
  	iSize--;
  }

}