#! /bin/ksh

# kill children with forced option

kill_process_recursively()
{
        for pid in $(eval "ps -ef | grep ' $1 ' | grep -v grep | grep -v batchjobkill" | awk '{printf "%s\n", $2}' | grep -v $1)
        {
                kill_process_recursively $pid false
        }

        if [ "$2" = "false" ] ; then
                kill -9 $1
        else
                kill $1
        fi
}

kill_process_recursively $1 true

