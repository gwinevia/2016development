#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define N 16

void calc_even(int num,int *even);
int main(void)
{
	int num[N];
	int even = 0,odd = 0;
	int i = N-1;
	int ans = 0;

	printf("ナンバーを入力:\n");
	num[0] = 0;
	while(i > 0){
		scanf("%d",&num[i]);
		i--;
	}

	// for(i=0;i<N;i++){
	// 	printf("%d\n",num[i]);
	// }

	for(i=0;i<N;i++){
		if((i+1)%2 == 0){
			calc_even(num[i],&even);
		}else{
			odd += num[i];
		}
	}
	printf("%d %d\n",odd,even);
  ans = 10 - ((even + odd) % 10);
  printf("answer = %d\n",ans);

	return 0;
}

void calc_even(int num,int *even)
{
	int tmp = 0;

	num = num * 2;
	if(num < 10){
		*even += num;
	}else{
		tmp = num%10 + num/10;
		*even += tmp;
	}

}