#!/usr/bin/env bash

PRIMARY='\033[0;36m'
ACCENT='\033[0;33m'
WARN='\033[1;31m'
NC='\033[0m'

accentEnd(){
    printf "${ACCENT}\n\n=================================================\n${NC}"
}

accentLog(){
    printf "${ACCENT}=================================================\n$1\n=================================================${NC}\n"
}

warnLog(){
    printf "${WARN}=================================================\n$1\n=================================================\n${NC}"
}

# 7 * 24 * 60 * 60
weekGap=604800

# argument weeks
weekCount=${1:-1}

# 今天
today=`date +'%Y-%m-%d'`

# 本周一时间戳
mondayTimestamp=`date -v-monday +%s`

# user
u=`git config user.email`

# format
f="%h - %an, %ar : %s"

process(){
    weekFromTimestamp=$[mondayTimestamp - weekGap * $1]
    weekToTimestamp=$[weekFromTimestamp + weekGap]

    fromDate=`date -r ${weekFromTimestamp} +%Y-%m-%d`
    toDate=`date -r ${weekToTimestamp} +%Y-%m-%d`

    if [[ $1 == 0 ]]
    then
        accentLog "${fromDate} 至 ${toDate} [本周] 我提交的 commit"
    else
        accentLog "${fromDate} 至 ${toDate} [${1} 周前] 我提交的 commit"
    fi

    echo "git log --after="${fromDate} 00:00:00" --before="${toDate} 00:00:00" --pretty=format:"${f}" --author="${u}" | cat"

    git log --after="${fromDate} 00:00:00" --before="${toDate} 00:00:00" --pretty=format:"${f}" --author="${u}" | cat

    echo
    echo
}

loopWeeks(){
    for (( i=0; i<${weekCount}; i++ ))
    do
        process ${i}
    done
}

case ${weekCount} in
    ''|*[!0-9]*) warnLog "参数需为数字" ;;
    *) loopWeeks ;;
esac