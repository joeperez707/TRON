ffs : ffs.o tron.o
	gcc -g -o ffs ffs.o tron.o `pkg-config fuse --libs`

ffs.o : ffs.c params.h
	gcc -g -Wall `pkg-config fuse --cflags` -c ffs.c

tron.o : tron.c tron.h params.h
	gcc -g -Wall `pkg-config fuse --cflags` -c tron.c

clean:
	rm -f ffs *.o

