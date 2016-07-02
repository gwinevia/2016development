#!/bin/bash
 
basename=$1 #引数からファイル名を受取る
filename=${basename%.*} #拡張子を取った名前

if [ -a ${filename}.c ]; then

    gcc -o ${filename} ${filename}.c

    if [ -a ${filename} ]; then

        ./${filename}

    else
        echo "** ${filename} do not made **"
    fi
else
    echo "** ${filename}.c does not exist **"
fi