#!/bin/bash


# smart-flow.sh -f smart-flow.yml -t -Dkey=value -classpath/c xxx.jar
# java -Dkey=value -cp xxx.jar -jar bootstrap.jar Main -f smart-flow.yml -t

dir=$(dirname $(dirname "$0"))
libdir="$dir/lib"
jvm_opt=""
classpath_opt="-classpath "
main_class="org.smartboot.plugin.bootstrap.Main"
program_opt=""

code2=0
for file in `ls $libdir` ; do
  #echo $file
  if [[ $file =~ .*jar$ && code2 == 0 ]]
    then
      classpath_opt="$classpath_opt$libdir/$file"
      code2=1
    else
      classpath_opt="$classpath_opt:$libdir/$file"
  fi
done

#if [ $# -le 1 ]
#then
#  echo "invalid param"
#  exit 1
#fi

code=1
for i in $*; do
  if [[ "$i" =~ ^-D.* || "$i" =~ ^-cp.* || "$i" =~ ^-classpath.* ]]
    then
      if [[ "$i" =~ ^-cp.* || "$i" =~ ^-classpath.* ]]
        then
          code=0
        else
          jvm_opt="$jvm_opt $i"
      fi
    elif [[ $code == 0 && "$i" =~ ^-.* ]]; then
      program_opt="$program_opt $i"
      code=1
    elif [ $code == 0 ]; then
      classpath_opt="$classpath_opt:$i"
    else
      program_opt="$program_opt $i"
      code=1
  fi
done

java $classpath_opt $jvm_opt $main_class $program_opt
