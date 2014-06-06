#define FUSE_USE_VERSION    26
#include <fuse.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>

static struct fuse_operations ffs_oper;
/*
static struct fuse_operations ffs_oper = {
	.getattr    = ffs_getattr,
	.readlink   = ffs_readlink,
	.readdir    = ffs_readdir,
	.open	    = ffs_open,
	.read	    = ffs_read,
	.write	    = ffs_write,
	.mknod	    = ffs_mknod,
	.unlink	    = ffs_unlink,
	.mkdir	    = ffs_mkdir,
	.rmdir	    = ffs_rmdir,
	.symlink    = ffs_symlink,
	.chmod	    = ffs_chmod,
	.chown	    = ffs_chown
};
*/


int main(int argc, char** argv)
{
	return fuse_main(argc, argv, &ffs_oper, NULL);
}
