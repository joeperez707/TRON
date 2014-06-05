#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include "tron.h"
#include "params.h"

struct node {
	char name[30];
	char location[100];
	struct sockaddr addr;
};

static node_t nodes[HT_SIZE];

int tron_init(void)
{
	int i;
	for (i = 0; i < HT_SIZE; i++)
		memset(&nodes[i], 0, sizeof(node_t));
	return 0;
}
