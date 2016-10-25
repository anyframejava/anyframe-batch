#! /bin/sh

. ./common.env

umask 00

EXE_BIN -cp $CLASSPATH com.sds.anyframe.batch.launcher.BatchJobLauncher "$@"

exit $?