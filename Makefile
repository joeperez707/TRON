.PHONY: all
all:   tron tags

tron : tronfs.o tron.o
	gcc -g -Wall -Werror -Wl -o tron tronfs.o tron.o `pkg-config fuse --libs`

tronfs.o : tronfs.c params.h
	gcc -g -Wall -Werror `pkg-config fuse --cflags` -c tronfs.c

tron.o : tron.c tron.h params.h
	gcc -g -Wall -Werror `pkg-config fuse --cflags` -c tron.c

.PHONY: tags
tags:
	ctags -Rb
	cscope -Rb

.PHONY: clean
clean:
	rm -f tron *.o tags cscope.out
