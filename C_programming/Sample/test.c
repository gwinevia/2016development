#include <stdio.h>
#include <stdlib.h>

struct taisen
{
	char team[37];
	int win_count[12];
	int lose_count[12];
	int draw_count[12];
	int win;
	int lose;
	int draw;
	double wpct;
};

int main(void)
{
	FILE *fp;
	char str[250];
	struct taisen temp;

	struct taisen data[6];
	int iLoop,jLoop,eLoop;

	if((fp=fopen("Pa2016.txt","r"))==NULL){
		printf("err! file cannot open!");
		exit(1);
	}

	for(iLoop=0;iLoop<6;iLoop++){
		fscanf(fp,"%s",data[iLoop].team);
		data[iLoop].win = 0;
		data[iLoop].lose = 0;
		data[iLoop].draw = 0;

		for(jLoop=0;jLoop<12;jLoop++){
			fscanf(fp,"%d",&data[iLoop].win_count[jLoop]);
			fscanf(fp,"%d",&data[iLoop].lose_count[jLoop]);
			fscanf(fp,"%d",&data[iLoop].draw_count[jLoop]);

			data[iLoop].win = data[iLoop].win + data[iLoop].win_count[jLoop];
			data[iLoop].lose = data[iLoop].lose + data[iLoop].lose_count[jLoop];
			data[iLoop].draw = data[iLoop].draw + data[iLoop].draw_count[jLoop];
		}

		data[iLoop].wpct = (double)data[iLoop].win / (data[iLoop].win + data[iLoop].lose );

	}	
  
  fclose(fp);

	for(iLoop=0;iLoop<6;iLoop++){
		for(jLoop=iLoop+1;jLoop<6;jLoop++){
			if(data[iLoop].wpct < data[jLoop].wpct){
				temp = data[iLoop];
				data[iLoop] = data[jLoop];
				data[jLoop] = temp;
			}
		}
	}

	if((fp=fopen("Pa2016result.txt","w"))==NULL){
		printf("err! file cannot open!");
		exit(1);
	}

	fprintf(fp,"順位 チーム名 勝率\n");
	for(eLoop=0;eLoop<6;eLoop++){
		fprintf(fp,"%d %-10s %.3f\n",eLoop+1,data[eLoop].team,data[eLoop].wpct);
	}
  fclose(fp);


	return 0;
}