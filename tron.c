#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "tron.h"
#include "params.h"

struct node {
	unsigned char id;
	char name[30];
	char location[100];
	struct sockaddr addr;
};

static node_t nodes[HT_SIZE] = { {0} };

static unsigned char registered_nodes = 0;

static unsigned long hash(unsigned char *str)
{
	int c = *str++;
	unsigned long hash = 0;
	while (c) {
		hash = hash * 33 ^ c;
		c = *str++;
	}
	return hash % registered_nodes;
}

#define TRUE	1
#define FALSE	0
int tron_init(void)
{
	(void)nodes;
	(void)hash;
	int opt = TRUE;
	struct sockaddr_in address;
	fd_set readfds;
	int master_socket = socket(AF_INET, SOCK_STREAM, 0);
	int sd = 0, max_sd = 0, i, activity, new_socket, addrlen;
	int client_socket[MAX_CLIENT] = { 0 };

	if (0 == master_socket) {
		perror("socket failed");
		exit(EXIT_FAILURE);
	}

	if (setsockopt(master_socket, SOL_SOCKET, SO_REUSEADDR,
		       &opt, sizeof(opt)) < 0) {
		perror("setsockopt");
		exit(EXIT_FAILURE);
	}
	address.sin_family = AF_INET;
	address.sin_addr.s_addr = INADDR_ANY;
	address.sin_port = htons(PORT);

	if (bind(master_socket, (struct sockaddr *)&address, sizeof(address)) <
	    0) {
		perror("bind failed");
		exit(EXIT_FAILURE);
	}

	if (listen(master_socket, 3) < 0) {
		perror("listen");
		exit(EXIT_FAILURE);
	}

	while (TRUE) {
		FD_ZERO(&readfds);
		FD_SET(master_socket, &readfds);
		max_sd = master_socket;

		for (i = 0; i < MAX_CLIENT; i++) {
			sd = client_socket[i];
			if (sd > 0)
				FD_SET(sd, &readfds);
			if (sd > max_sd)
				max_sd = sd;
		}

		activity = select(max_sd + 1, &readfds, NULL, NULL, NULL);
		if ((activity < 0) && (EINTR != errno))
			perror("select error");
		if (FD_ISSET(master_socket, &readfds)) {
			new_socket = accept(master_socket,
						 (struct sockaddr *)&address,
						 (socklen_t *) & addrlen);
			if (new_socket < 0) {
				perror("accept");
				exit(EXIT_FAILURE);
			}

			client_socket[connected_clients] = new_socket;
			connected_clients++;
		} else {
			for (i = 0; i < MAX_CLIENTS; i++) {
				sd = client_socket[i];
				if (FD_ISSET(sd, &readfds)) {
					if ((msg = read(sd, buffer, 1024)) ==0){
						close(sd);
						client_socket[i] = 0;
					} else {
						resp = handle_msg(msg);
						send(sd, resp, strlen(resp), 0);
					}
				}
			}
		}
	}
	return 0;
}
